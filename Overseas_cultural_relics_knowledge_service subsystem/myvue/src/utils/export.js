const EXPORT_COLUMNS = [
  { key: 'id', label: 'ID' },
  { key: 'object_name', label: '名称' },
  { key: 'cat1', label: '材质' },
  { key: 'cat2', label: '朝代' },
  { key: 'cat3', label: '分类' },
  { key: 'makers_name', label: '作者' },
  { key: 'img_url', label: '图片' }
]

function escapeCsvCell (value) {
  const str = value == null ? '' : String(value)
  if (/[",\n\r]/.test(str)) {
    return `"${str.replace(/"/g, '""')}"`
  }
  return str
}

function downloadBlob (blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

export function exportToCsv (rows, filename = 'search-results.csv') {
  if (!rows?.length) return false
  const header = EXPORT_COLUMNS.map((c) => c.label).join(',')
  const lines = rows.map((row) =>
    EXPORT_COLUMNS.map((c) => escapeCsvCell(row[c.key])).join(',')
  )
  const bom = '\uFEFF'
  const blob = new Blob([bom + header + '\n' + lines.join('\n')], {
    type: 'text/csv;charset=utf-8'
  })
  downloadBlob(blob, filename)
  return true
}

export function exportToJson (rows, filename = 'search-results.json') {
  if (!rows?.length) return false
  const blob = new Blob([JSON.stringify(rows, null, 2)], {
    type: 'application/json;charset=utf-8'
  })
  downloadBlob(blob, filename)
  return true
}
