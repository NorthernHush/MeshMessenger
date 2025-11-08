import create from 'zustand'

type State = { token: string | null, setToken: (t:string|null)=>void }
export const useAuthStore = create<State>((set)=>({ token: null, setToken: (t)=>set({token:t}) }))
