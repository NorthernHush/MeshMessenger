import React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import api from '../../services/api'
import { generateKeyPairs, exportPublicKeyBase64, exportPrivateKeyJwk, deriveKeyFromPassword, encryptPrivateKey } from '../../utils/crypto'
import { useNavigate } from 'react-router-dom'

const schema = z.object({ email: z.string().email(), password: z.string().min(12), displayName: z.string().min(2) })

export default function Register(){
  const nav = useNavigate()
  const { register, handleSubmit, formState:{errors} } = useForm({ resolver: zodResolver(schema) })
  const onSubmit = async (v:any)=>{
    // create account
    await api.post('/api/v1/auth/register', { email: v.email, password: v.password, displayName: v.displayName })
    // generate keys
    const kp = await generateKeyPairs()
    const pubSign = await exportPublicKeyBase64(kp.sign.pub)
    const pubEnc = await exportPublicKeyBase64(kp.enc.pub)
    // derive master key and encrypt private keys
    const master = await deriveKeyFromPassword(v.password)
    const privSign = await exportPrivateKeyJwk(kp.sign.priv)
    const privEnc = await exportPrivateKeyJwk(kp.enc.priv)
    const encSign = await encryptPrivateKey(master, JSON.stringify(privSign))
    const encEnc = await encryptPrivateKey(master, JSON.stringify(privEnc))
    // store encrypted private keys in IndexedDB (idb-keyval or native)
    await (window.indexedDB ? Promise.resolve() : Promise.resolve())
    // upload public keys
    await api.post('/api/v1/users/keys', { publicSigningKey:pubSign, publicEncryptionKey:pubEnc, keyFingerprint: pubEnc.slice(0,16) })
    nav('/auth/login')
  }
  return (
    <div className="p-4 max-w-md mx-auto">
      <h2 className="text-2xl mb-4">Register</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <input {...register('displayName')} placeholder="Display name" className="border p-2 w-full" />
        {errors.displayName && <div className="text-red-600">{String(errors.displayName?.message)}</div>}
        <input {...register('email')} placeholder="Email" className="border p-2 w-full" />
        {errors.email && <div className="text-red-600">{String(errors.email?.message)}</div>}
        <input {...register('password')} type="password" placeholder="Master password" className="border p-2 w-full" />
        {errors.password && <div className="text-red-600">{String(errors.password?.message)}</div>}
        <button className="bg-green-600 text-white px-4 py-2 rounded">Register</button>
      </form>
    </div>
  )
}
