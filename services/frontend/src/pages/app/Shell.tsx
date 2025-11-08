import React from 'react'
import ChannelsList from '../../ui/ChannelsList'
import ChatPanel from '../../ui/ChatPanel'
import ChannelMeta from '../../ui/ChannelMeta'

export default function Shell(){
  return (
    <div className="h-screen grid grid-cols-4 gap-2">
      <div className="col-span-1 border-r"><ChannelsList/></div>
      <div className="col-span-2"><ChatPanel/></div>
      <div className="col-span-1 border-l"><ChannelMeta/></div>
    </div>
  )
}
