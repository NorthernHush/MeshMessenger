export async function generateKeyPairs(){
  const sign = await window.crypto.subtle.generateKey({ name:'NODE-ED25519' } as any, true, ['sign','verify']).catch(async ()=>{
    // Use Ed25519 via namedParam in modern browsers
    return await window.crypto.subtle.generateKey({ name: 'Ed25519' } as any, true, ['sign','verify'])
  })
  const enc = await window.crypto.subtle.generateKey({ name:'X25519' } as any, true, ['deriveKey','deriveBits']).catch(async ()=>{
    return await window.crypto.subtle.generateKey({ name:'X25519' } as any, true, ['deriveKey','deriveBits'])
  })
  // sign: { pub, priv }, enc: { pub, priv }
  // @ts-ignore
  return { sign: { pub: sign.publicKey, priv: sign.privateKey }, enc: { pub: enc.publicKey, priv: enc.privateKey } }
}

export async function exportPublicKeyBase64(key: CryptoKey){
  const sp = await window.crypto.subtle.exportKey('spki', key)
  return btoa(String.fromCharCode(...new Uint8Array(sp)))
}

export async function exportPrivateKeyJwk(key: CryptoKey){
  return await window.crypto.subtle.exportKey('jwk', key)
}

export async function deriveKeyFromPassword(password: string){
  const pw = new TextEncoder().encode(password)
  const salt = new Uint8Array(16)
  window.crypto.getRandomValues(salt)
  const base = await window.crypto.subtle.importKey('raw', pw, 'PBKDF2', false, ['deriveKey'])
  return await window.crypto.subtle.deriveKey({ name:'PBKDF2', salt, iterations: 200000, hash:'SHA-256' }, base, { name:'AES-GCM', length:256 }, false, ['encrypt','decrypt'])
}

export async function encryptPrivateKey(aesKey: CryptoKey, data: string){
  const iv = window.crypto.getRandomValues(new Uint8Array(12))
  const ct = await window.crypto.subtle.encrypt({ name:'AES-GCM', iv }, aesKey, new TextEncoder().encode(data))
  return { iv: btoa(String.fromCharCode(...new Uint8Array(iv))), payload: btoa(String.fromCharCode(...new Uint8Array(ct))) }
}
