package com.redriver.modbus;

/**
 * �Ĵ���״̬ ������
 * Created by zwq00000 on 2015/6/21.
 */
public interface Holder<T>  {

  /**
   * ���� �Ĵ��� ��ֵ
   * @param offset
   * @param value
   */
  void setValue(int offset, T value);

  /**
   * ��ȡ�Ĵ��� ԭʼֵ
   * @param offset
   * @return
   */
  T getValue(int offset);

  /**
   * ��ȡ��վId    {@value 1~255}
   * @return
   */
  byte getSlaveId();


  /**
   * �Ĵ��� ��ʼλ��
   * @return
   */
  short getStartNum();

  /**
   * �Ĵ��� ����
   * @return
   */
  short getCount();

  /**
   * ��ȡ֮ǰ ���üĴ�����ֵ
   */
  void reset();
}
