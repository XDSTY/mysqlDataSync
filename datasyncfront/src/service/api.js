import vue from 'vue'
import qs from 'qs'
import axios from 'axios'

export const baseUrl = '/datasync'
const get = (path, query) => {
  return vue.prototype.$request(`${baseUrl}${path}`, query)
}

const post = (path, body) => {
  const reqBody = {}

  if (body instanceof Array) {
    // 如果是数组的话，统一从服务端用名为list的字符串来接收
    reqBody.list = JSON.stringify(body)
  } else {
    for (const key in body) {
      reqBody[key] = typeof body[key] === 'string' ? body[key].trim() : body[key]
    }
  }

  return axios.post(`${baseUrl}${path}`, reqBody)
}

export const datasync = {
  sync: query => post(`/sync`, query)
}