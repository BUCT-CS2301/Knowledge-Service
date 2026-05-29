/** 按文物类型/名称关键词返回统一占位图（SVG data URI），避免 picsum 随机风景与标题不符 */

const PALETTES = {
  bronze: { bg: '#3d2914', fg: '#c9a86c', label: '青铜' },
  porcelain: { bg: '#1a3a52', fg: '#a8d4e6', label: '陶瓷' },
  ceramic: { bg: '#2d4a3e', fg: '#b8e0c8', label: '陶瓷' },
  jade: { bg: '#1e3d32', fg: '#7dcea0', label: '玉器' },
  painting: { bg: '#4a2c2a', fg: '#e8c4a0', label: '书画' },
  default: { bg: '#4a3728', fg: '#d4b896', label: '文物' }
}

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

/** 列表项展示用：优先真实 URL，否则类型占位图 */
export function getArtifactImageUrl (item) {
  const url = item?.img_url
  if (url && String(url).trim() && !String(url).includes('picsum.photos')) {
    return url
  }
  return svgDataUri(pickPalette(item), item?.object_name)
}
