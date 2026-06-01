/** Java String.hashCode 与后端 collect.user_collect_id 对齐 */
export function relicKeyFromObjectId (objectId) {
  if (!objectId) return 0
  let hash = 0
  for (let i = 0; i < objectId.length; i++) {
    hash = ((hash << 5) - hash) + objectId.charCodeAt(i)
    hash |= 0
  }
  return hash
}
