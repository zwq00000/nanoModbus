package com.redriver.modbus;

public interface WriteableHolder<T> extends  Holder<T>{

  /**
   * ����ȫ������
   * @return
   */
  byte[] toBytes();

  /**
   *  ��ȡ �Ĵ���״̬����֡ ռ���ֽ���
   * ���� ��ʼ��ַ + ���� + �ֽ��� + ״̬�ֽ�����
   * @return
   */
  int size();
}
