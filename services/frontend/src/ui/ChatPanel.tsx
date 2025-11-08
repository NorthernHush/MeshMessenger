import React, { useState } from 'react'
import api from '../services/api'
import { generateKeyPairs, exportPublicKeyBase64 } from '../utils/crypto'

export default function ChatPanel(){
  const [text,setText] = useState('')
  const send = async ()=>{
    if(!text) return
    // stub: send plaintext as payload (replace with hybrid encryption)
    await api.post('/api/v1/channels/1/messages', { payload: btoa(text), nonce: btoa('0000'), senderKeyFingerprint: 'fp', recipientKeyFingerprints: ['fp2'], signature: null, encrypted:false })
    setText('')
  }
  return (
    <div className="h-full flex flex-col">
      <div className="flex-1 p-2 overflow-auto">Messages (stub)</div>
      <div className="p-2 flex gap-2">
        <input value={text} onChange={e=>setText(e.target.value)} className="flex-1 border p-2" />
        <button onClick={send} className="bg-blue-600 text-white px-3 rounded">Send</button>
      </div>
    </div>
  )
}
