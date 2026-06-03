package com.service.Impl;

import com.entity.Cart;
import com.mapper.CartMapper;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.ArtifactImageFallbackService;
import com.service.ICartService;
import com.service.Neo4jArtifactSearchService;
import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FindImageMapper findImageMapper;
    @Autowired
    private Neo4jArtifactSearchService neo4jArtifactSearchService;
    @Autowired
    private ArtifactImageFallbackService artifactImageFallbackService;

    @Override
    public List<Cart> SearchProductByClass(String x, String y) {
        List<Cart> result = switch (x) {
            case "museum" -> neo4jArtifactSearchService.searchByMuseum(y);
            case "mart" -> neo4jArtifactSearchService.searchByMaterial(y);
            case "dynasty" -> neo4jArtifactSearchService.searchByPeriod(y);
            case "use" -> neo4jArtifactSearchService.searchByType(y);
            default -> throw new SecurityException("查询错误");
        };
        return requireNonEmpty(postProcessImages(result));
    }

    @Override
    public List<Cart> SearchProductBySort(String x) {
        List<Cart> result = switch (x) {
            case "wordUp" -> neo4jArtifactSearchService.sortByName(true);
            case "wordDown" -> neo4jArtifactSearchService.sortByName(false);
            case "timeUp" -> neo4jArtifactSearchService.sortByPeriod(true);
            case "timeDown" -> neo4jArtifactSearchService.sortByPeriod(false);
            default -> throw new SecurityException("查询错误");
        };
        return requireNonEmpty(postProcessImages(result));
    }

    @Override
    public List<Cart> SearchProductByMulti(String museum, String cat1, String cat2, String cat3) {
        List<Cart> result = neo4jArtifactSearchService.searchByMulti(museum, cat1, cat2, cat3);
        return requireNonEmpty(postProcessImages(result));
    }

    @Override
    public List<Cart> SearchProductObscure(String str) {
        List<Cart> result = neo4jArtifactSearchService.searchByKeyword(str);
        return requireNonEmpty(postProcessImages(result));
    }

    @Override
    public List<Cart> SearchCommentView(String museum, String cat2, String makers_name, String geography, BigInteger id) {
        // 相关推荐仍使用本地 MySQL 演示数据
        List<Cart> commentView = new ArrayList<>();
        int n = 4;
        Cart cart;
        List<BigInteger> a1 = new ArrayList<>();
        cart = cartMapper.findBygeography(geography, id, 0, 1);
        if (cart != null) {
            commentView.add(resolveImage(cart));
            a1.add(cart.getId());
            n--;
        }
        cart = cartMapper.findBytime1(cat2, id, 0, 1);
        if (cart != null) {
            for (int i = 0; i < a1.size(); i++) {
                if (cart == null) {
                    break;
                }
                if (cart.getId().equals(a1.get(i))) {
                    cart = cartMapper.findBygeography(cat2, id, a1.get(i).intValue(), 1);
                    i = -1;
                }
            }
            if (cart != null) {
                commentView.add(resolveImage(cart));
                a1.add(cart.getId());
                n--;
            }
        }
        cart = cartMapper.findBymakername1(makers_name, id, 0, 1);
        if (cart != null) {
            for (int i = 0; i < a1.size(); i++) {
                if (cart == null) {
                    break;
                }
                if (cart.getId().equals(a1.get(i))) {
                    cart = cartMapper.findBymakername1(makers_name, id, a1.get(i).intValue(), 1);
                    i = -1;
                }
            }
            if (cart != null) {
                commentView.add(resolveImage(cart));
                a1.add(cart.getId());
                n--;
            }
        }
        while (n != 0) {
            cart = cartMapper.findByMuseum1(museum, id, 0, 1);
            if (cart != null) {
                for (int i = 0; i < a1.size(); i++) {
                    if (cart == null) {
                        break;
                    }
                    if (cart.getId().equals(a1.get(i))) {
                        cart = cartMapper.findByMuseum1(museum, id, a1.get(i).intValue(), 1);
                        i = -1;
                    }
                }
            }
            if (cart == null) {
                break;
            }
            commentView.add(resolveImage(cart));
            a1.add(cart.getId());
            n--;
        }
        while (n != 0) {
            cart = cartMapper.findone(id, 0, 1);
            for (int i = 0; i < a1.size(); i++) {
                if (cart == null) {
                    break;
                }
                if (cart.getId().equals(a1.get(i))) {
                    cart = cartMapper.findone(id, a1.get(i).intValue(), 1);
                    i = -1;
                }
            }
            if (cart != null) {
                commentView.add(resolveImage(cart));
            }
            n--;
        }
        return commentView;
    }

    private List<Cart> requireNonEmpty(List<Cart> result) {
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    private List<Cart> postProcessImages(List<Cart> result) {
        artifactImageFallbackService.fillMissingImages(result);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, resolveImage(result.get(i)));
        }
        return result;
    }

    private Cart resolveImage(Cart cart) {
        if (cart == null || cart.getImg_url() == null || cart.getImg_url().isBlank()) {
            return cart;
        }
        String url = cart.getImg_url();
        if (url.contains(",")) {
            url = url.split(",")[0];
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            cart.setImg_url(url);
            return cart;
        }
        try {
            cart.setImg_url(findImageMapper.findImage(url));
        } catch (Exception ignored) {
            cart.setImg_url(null);
        }
        return cart;
    }
}
