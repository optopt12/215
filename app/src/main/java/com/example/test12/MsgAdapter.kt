package com.example.test12

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test12.databinding.MsgLeftLayoutBinding
import com.example.test12.databinding.MsgRightLayoutBinding
import java.lang.IllegalArgumentException

class MsgAdapter(private val msgList: List<Msg>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //载入右聊天框布局控件
    inner class RightViewHolder(val binding: MsgRightLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    //载入左聊天框布局控件
    inner class LeftViewHolder(val binding: MsgLeftLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    //获取消息类型(左或者右),返回到onCreateViewHolder()方法的viewType参数里面
    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position] //根据当前数据源的元素类型
        return msg.type
    }

    //根据viewType消息类型的不同,构建不同的消息布局(左&右)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Msg.LEFT) {
            val botView = MsgLeftLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftViewHolder(botView) //返回控件+布局
        } else {
            val rightView = MsgRightLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightViewHolder(rightView)
        }

    }

    //对聊天控件的消息文本进行赋值
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.binding.botText.text = msg.content
                Log.e("onBindViewHolder", "onBindViewHolder: LeftViewHolder")
            }
            is RightViewHolder -> {
                holder.binding.huText.text = msg.content
                Log.e("onBindViewHolder", "onBindViewHolder: RightViewHolder")
            }
            else -> {
                Log.e("onBindViewHolder", "onBindViewHolder: ELSE")
            }
        }
    }

    //返回项数
    override fun getItemCount(): Int {
        return msgList.size
    }
}