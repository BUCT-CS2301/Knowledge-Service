/** 时间轴策展数据：图片均为 public/timeline/ 下本地静态资源，可手动替换同名文件 */
const img = (file) => `/timeline/${file}`

export const timelineMockData = [
  {
    dynasty: '远古',
    year: '约公元前5000-2000年',
    description: '新石器时代，彩陶文化繁荣，玉器制作技艺开始发展。',
    relics: [
      { name: '彩陶盆', type: '陶瓷', museum: '中国国家博物馆', image: img('painted-pottery.png'), description: '新石器时代仰韶文化典型彩陶器，绘有鱼纹等图案，反映先民渔猎生活与审美。' },
      { name: '玉琮', type: '玉器', museum: '大英博物馆', image: img('jade-cong.png'), description: '良渚文化代表性玉礼器，外方内圆，象征天地沟通，是早期玉文化的重要见证。' }
    ]
  },
  {
    dynasty: '夏商',
    year: '约公元前2000-1046年',
    description: '青铜时代早期，甲骨文出现，青铜礼器开始盛行。',
    relics: [
      { name: '青铜兽面纹鼎', type: '青铜器', museum: '故宫博物院', image: img('bronze-ding.png'), description: '商代青铜礼器代表，兽面纹威严庄重，体现青铜铸造与礼制文化的高度发展。' },
      { name: '甲骨文', type: '文字', museum: '中国国家博物馆', image: img('oracle-bone.png'), description: '商代刻在龟甲兽骨上的文字，是中国已知最早的成熟文字系统之一。' }
    ]
  },
  {
    dynasty: '西周',
    year: '公元前1046-771年',
    description: '礼乐制度确立，青铜器铭文发达，玉器工艺精湛。',
    relics: [
      { name: '毛公鼎', type: '青铜器', museum: '台北故宫博物院', image: img('bronze-maogong.png'), description: '西周晚期青铜鼎，内壁铭文近五百字，是研究西周历史的重要史料。' },
      { name: '玉圭', type: '玉器', museum: '大英博物馆', image: img('jade-gui.png'), description: '古代玉器礼器，用于朝聘、祭祀等礼仪场合，体现周代礼制规范。' }
    ]
  },
  {
    dynasty: '春秋战国',
    year: '公元前770-221年',
    description: '百家争鸣，青铜器走向世俗化，漆器工艺兴起。',
    relics: [
      { name: '越王勾践剑', type: '青铜器', museum: '湖北省博物馆', image: img('bronze-sword.png'), description: '春秋晚期越国青铜剑，历经两千余年仍锋利，被誉为"天下第一剑"。' },
      { name: '曾侯乙编钟', type: '青铜器', museum: '湖北省博物馆', image: img('bronze-bells.png'), description: '战国早期大型青铜编钟，音域宽广，展现古代中国高度发达的青铜铸造与音乐文化。' }
    ]
  },
  {
    dynasty: '秦汉',
    year: '公元前221-220年',
    description: '统一王朝建立，陶瓷、漆器工艺发展，丝绸之路开始形成。',
    relics: [
      { name: '秦兵马俑', type: '陶俑', museum: '秦始皇兵马俑博物馆', image: img('terracotta.png'), description: '秦始皇陵陪葬陶俑群，规模宏大，被誉为"世界第八大奇迹"。' },
      { name: '马王堆帛画', type: '绘画', museum: '湖南省博物馆', image: img('silk-painting.png'), description: '西汉早期帛画，描绘墓主灵魂升天场景，是研究汉代绘画与信仰的重要实物。' }
    ]
  },
  {
    dynasty: '三国两晋',
    year: '220-589年',
    description: '战乱频繁但文化繁荣，佛教艺术传入，绘画书法发展。',
    relics: [
      { name: '顾恺之女史箴图', type: '书画', museum: '大英博物馆', image: img('gu-kaizhi-scroll.png'), description: '东晋画家顾恺之传世名作（唐摹本），中国早期人物画代表作，现藏大英博物馆。' },
      { name: '青瓷莲花尊', type: '陶瓷', museum: '故宫博物院', image: img('celadon-lotus.png'), description: '南北朝时期青瓷精品，莲花装饰典雅，反映当时青瓷烧制工艺的高超水平。' }
    ]
  },
  {
    dynasty: '隋唐',
    year: '581-907年',
    description: '盛世繁荣，唐三彩、青花瓷兴起，中外文化交流频繁。',
    relics: [
      { name: '唐三彩骆驼', type: '陶瓷', museum: '故宫博物院', image: img('tang-sancai.png'), description: '唐代三彩釉陶骆驼俑，色彩绚丽，反映丝绸之路贸易与唐代雕塑艺术。' },
      { name: '敦煌壁画', type: '绘画', museum: '敦煌研究院', image: img('dunhuang-mural.png'), description: '敦煌莫高窟壁画，融合佛教艺术与中原风格，是丝绸之路文化交流的珍贵遗产。' }
    ]
  },
  {
    dynasty: '五代十国',
    year: '907-960年',
    description: '政权更迭频繁，但艺术持续发展，绘画成就突出。',
    relics: [
      { name: '韩熙载夜宴图', type: '书画', museum: '故宫博物院', image: img('han-xizai-banquet.png'), description: '五代南唐画家顾闳中名作（宋摹本），生动描绘韩熙载夜宴场景，人物画巅峰之作。' },
      { name: '越窑青瓷', type: '陶瓷', museum: '上海博物馆', image: img('yue-celadon.png'), description: '五代时期越窑青瓷，釉色温润，"秘色瓷"代表，体现当时制瓷工艺的精湛。' }
    ]
  },
  {
    dynasty: '宋元',
    year: '960-1368年',
    description: '瓷器工艺达到顶峰，五大名窑闻名于世，文人书画兴盛。',
    relics: [
      { name: '汝窑青瓷', type: '陶瓷', museum: '大英博物馆', image: img('ru-ware.png'), description: '北宋汝窑天青釉瓷器，釉色如雨过天晴，为宋代五大名窑之首。' },
      { name: '清明上河图', type: '书画', museum: '故宫博物院', image: img('qingming-scroll.png'), description: '北宋张择端绘制的风俗画长卷，生动再现汴京繁华景象，是中国绘画史上的瑰宝。' }
    ]
  },
  {
    dynasty: '明清',
    year: '1368-1912年',
    description: '官窑瓷器精美绝伦，珐琅彩、粉彩等新工艺出现。',
    relics: [
      { name: '青花瓷瓶', type: '陶瓷', museum: '大英博物馆', image: img('blue-white-vase.png'), description: '明清时期青花瓷器代表，白地蓝花，工艺精湛，深受海内外收藏者青睐。' },
      { name: '珐琅彩瓷', type: '陶瓷', museum: '大都会博物馆', image: img('enamel-porcelain.png'), description: '清代宫廷珐琅彩瓷器，色彩富丽，融合中西工艺，是官窑瓷器的巅峰之作。' }
    ]
  },
  {
    dynasty: '近现代',
    year: '1912-2000年',
    description: '近现代文物保护与收藏兴起，大量海外流失文物开始回流。',
    relics: [
      { name: '敦煌遗书', type: '文献', museum: '敦煌研究院', image: img('dunhuang-manuscript.png'), description: '敦煌莫高窟发现的古代文献，涵盖经卷、文书等，是研究中国古代文化的重要资料。' },
      { name: '圆明园兽首', type: '青铜器', museum: '保利艺术博物馆', image: img('yuanmingyuan-head.png'), description: '圆明园十二生肖兽首铜像之一，见证近代文物流散与回归的历史。' }
    ]
  }
]
