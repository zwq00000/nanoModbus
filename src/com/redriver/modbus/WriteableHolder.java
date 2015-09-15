package com.redriver.modbus;

public interface WriteableHolder<T> extends  Holder<T>{

  /**
   * 返回全部数据
   * @return
   */
  byte[] toBytes();

  /**
   *  获取 寄存器状态数据帧 占用字节数
   * 包括 起始地址 + 数量 + 字节数 + 状态字节数组
   * @return
   */
  int size();
}
