import React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import api from '../../services/api'
import { useAuthStore } from '../../stores/auth'

const schema = z.object({ email: z.string().email(), password: z.string().min(8) })

export default function Login(){
  const { setToken } = useAuthStore()
  const { register, handleSubmit, formState:{errors} } = useForm({ resolver: zodResolver(schema) })
  const onSubmit = async (v:any)=>{
    const res = await api.post('/api/v1/auth/login', v)
    setToken(res.data.accessToken)
  }
  return (
    <div className="p-4 max-w-md mx-auto">
      <h2 className="text-2xl mb-4">Login</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <input {...register('email')} placeholder="Email" className="border p-2 w-full" />
        {errors.email && <div className="text-red-600">{String(errors.email?.message)}</div>}
        <input {...register('password')} type="password" placeholder="Password" className="border p-2 w-full" />
        {errors.password && <div className="text-red-600">{String(errors.password?.message)}</div>}
        <button className="bg-blue-600 text-white px-4 py-2 rounded">Login</button>
      </form>
    </div>
  )
}
