package com.redriver.modbus;

/**
 * 寄存器状态 保持器
 * Created by zwq00000 on 2015/6/21.
 */
public interface Holder<T>  {

  /**
   * 设置 寄存器 数值
   * @param offset
   * @param value
   */
  void setValue(int offset, T value);

  /**
   * 读取寄存器 原始值
   * @param offset
   * @return
   */
  T getValue(int offset);

  /**
   * 获取从站Id    {@value 1~255}
   * @return
   */
  byte getSlaveId();


  /**
   * 寄存器 起始位置
   * @return
   */
  short getStartNum();

  /**
   * 寄存器 数量
   * @return
   */
  short getCount();

  /**
   * 读取之前 重置寄存器数值
   */
  void reset();
}
