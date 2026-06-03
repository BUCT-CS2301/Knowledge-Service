/** 按文物类型/名称关键词返回统一占位图（SVG data URI），避免 picsum 随机风景与标题不符 */

const PALETTES = {
  bronze: { bg: '#3d2914', fg: '#c9a86c', label: '青铜' },
  porcelain: { bg: '#1a3a52', fg: '#a8d4e6', label: '陶瓷' },
  ceramic: { bg: '#2d4a3e', fg: '#b8e0c8', label: '陶瓷' },
  jade: { bg: '#1e3d32', fg: '#7dcea0', label: '玉器' },
  painting: { bg: '#4a2c2a', fg: '#e8c4a0', label: '书画' },
  default: { bg: '#4a3728', fg: '#d4b896', label: '文物' }
}

const PROXY_HOSTS = ['art.nelson-atkins.org', 'penn.museum', 'clevelandart.org']

function pickPalette (item) {
  const text = [
    item?.cat1,
    item?.cat3,
    item?.object_name,
    item?.cat2
  ].filter(Boolean).join(' ').toLowerCase()

  if (/bronze|青铜|铜/.test(text)) return PALETTES.bronze
  if (/porcelain|瓷|陶|ceramic|三彩/.test(text)) return PALETTES.porcelain
  if (/jade|玉/.test(text)) return PALETTES.jade
  if (/paint|画|绢|纸/.test(text)) return PALETTES.painting
  if (/陶|釉/.test(text)) return PALETTES.ceramic
  return PALETTES.default
}

function svgDataUri (palette, title) {
  const name = (title || palette.label).slice(0, 8)
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
  <rect width="400" height="300" fill="${palette.bg}"/>
  <ellipse cx="200" cy="130" rx="72" ry="48" fill="${palette.fg}" opacity="0.35"/>
  <text x="200" y="200" text-anchor="middle" fill="${palette.fg}" font-size="22" font-family="sans-serif">${name}</text>
  <text x="200" y="232" text-anchor="middle" fill="${palette.fg}" opacity="0.75" font-size="14" font-family="sans-serif">${palette.label} · 示意图</text>
</svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
}

function museumLabel (item) {
  return [item?.makers_name, item?.museum, item?.museumName, item?.museum_name_cn].filter(Boolean).join(' ')
}

function isClevelandMuseum (museum) {
  return /Cleveland|克利夫兰/i.test(museum || '')
}

function looksLikeClevelandAccession (accession) {
  return /^\d{4}\..+/.test(accession) || /^\d{4}-.+/.test(accession)
}

/** 根据馆藏编号推断克利夫兰博物馆 CDN 地址 */
export function buildCdnUrlFromAccession (accession, museum) {
  const acc = (accession || '').trim()
  if (!acc) return ''
  const muse = museum || ''
  if (isClevelandMuseum(muse) || (!muse && looksLikeClevelandAccession(acc))) {
    return `https://openaccess-cdn.clevelandart.org/${acc}/${acc}_web.jpg`
  }
  return ''
}

export function buildCdnUrlFromItem (item) {
  const acc = (item?.accessionNumber || item?.accession_number || '').trim()
  return buildCdnUrlFromAccession(acc, museumLabel(item))
}

function needsProxy (url) {
  try {
    const host = new URL(url).hostname.toLowerCase()
    return PROXY_HOSTS.some((h) => host.includes(h))
  } catch (_) {
    return false
  }
}

export function toProxyUrl (url) {
  const stripped = url.replace(/^https?:\/\//i, '')
  return `https://images.weserv.nl/?url=${encodeURIComponent(stripped)}&output=jpg`
}

/** 外链图片：对防盗链域名走代理 */
export function normalizeExternalImageUrl (url) {
  if (!url || !/^https?:\/\//i.test(url)) return url
  return needsProxy(url) ? toProxyUrl(url) : url
}

/** 列表项展示用：真实 URL → CDN 推断 → 占位图 */
export function getArtifactImageUrl (item) {
  const url = item?.img_url
  if (url && String(url).trim() && !String(url).includes('picsum.photos')) {
    return normalizeExternalImageUrl(String(url).trim())
  }
  const cdn = buildCdnUrlFromItem(item)
  if (cdn) {
    return normalizeExternalImageUrl(cdn)
  }
  return svgDataUri(pickPalette(item), item?.object_name)
}

/** 占位图（仅在所有真实图源均失败时使用） */
export function getArtifactPlaceholderUrl (item) {
  return svgDataUri(pickPalette(item), item?.object_name)
}

/**
 * 图片加载失败时的多级重试：代理 → 原链 → CDN 推断 → 占位图
 */
export function handleArtifactImageError (e, item) {
  const raw = (item?.img_url || '').trim()
  if (raw && /^https?:\/\//i.test(raw)) {
    if (!e.target.dataset.triedProxy) {
      e.target.dataset.triedProxy = '1'
      e.target.src = toProxyUrl(raw)
      return
    }
    if (!e.target.dataset.triedDirect) {
      e.target.dataset.triedDirect = '1'
      e.target.src = raw
      return
    }
  }
  const cdn = buildCdnUrlFromItem(item)
  if (cdn && !e.target.dataset.triedCdn) {
    e.target.dataset.triedCdn = '1'
    e.target.src = normalizeExternalImageUrl(cdn)
    return
  }
  e.target.onerror = null
  e.target.src = getArtifactPlaceholderUrl(item)
}
