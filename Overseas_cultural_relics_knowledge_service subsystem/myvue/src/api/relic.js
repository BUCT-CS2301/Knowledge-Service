import request from './request'
import { relicKeyFromObjectId } from '@/utils/relicKey'

/**
 * 收藏文物（Neo4j UUID 或 MySQL 数字 rid）
 */
export function collectRelic ({ uid, objectId, relicName, rid }) {
  const payload = { uid: String(uid) }
  if (objectId) {
    payload.objectId = objectId
    payload.relicName = relicName || ''
    payload.rid = String(relicKeyFromObjectId(objectId))
  } else {
    payload.rid = String(rid)
  }
  return request.post('/search/searchById/collect', payload)
}

/**
 * 取消收藏
 */
export function uncollectRelic ({ uid, objectId, rid }) {
  const payload = { uid: String(uid) }
  if (objectId) {
    payload.objectId = objectId
    payload.rid = String(relicKeyFromObjectId(objectId))
  } else {
    payload.rid = String(rid)
  }
  return request.post('/user_admin/deleteCollect', payload)
}

/** 某文物下全部用户的评论（社区列表） */
export function getRelicComments ({ rid, objectId }) {
  const payload = {}
  if (objectId) {
    payload.objectId = objectId
    payload.rid = String(relicKeyFromObjectId(objectId))
  } else if (rid != null && rid !== '') {
    payload.rid = String(rid)
  }
  return request.post('/search/relicComments', payload)
}
