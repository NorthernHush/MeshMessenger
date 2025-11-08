import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const instance = axios.create({ baseURL: '' })

instance.interceptors.request.use(cfg=>{
  const token = useAuthStore.getState().token
  if(token) cfg.headers = { ...(cfg.headers||{}), Authorization: `Bearer ${token}` }
  return cfg
})

let isRefreshing = false
let pending: Array<() => void> = []
instance.interceptors.response.use(r=>r, async err=>{
  const res = err.response
  if(res && res.status === 401 && !isRefreshing){
    isRefreshing = true
    try{
      await instance.post('/api/v1/auth/refresh')
      isRefreshing = false
      pending.forEach(cb=>cb()); pending = []
      return instance(err.config)
    }catch(e){ isRefreshing=false; pending=[]; useAuthStore.getState().setToken(null); return Promise.reject(e) }
  }
  return Promise.reject(err)
})

export default instance
