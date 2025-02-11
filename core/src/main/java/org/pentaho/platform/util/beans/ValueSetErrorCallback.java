/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.platform.util.beans;

/**
 * This interface is used in conjunction with
 * {@link BeanUtil#setValue(Object, String, ValueSetCallback, PropertyNameFormatter...)} to give the developer full
 * control of how to handle various events that occur during the attempt to set a value on a Java Bean. Using a
 * callback allows us to always employ the same logic when syncing data to a bean (since that are loads of
 * different ways to do this just in commons-beanutils).
 * 
 * @author aphillips
 * @see BeanUtil#setValue(Object, String, ValueSetCallback, PropertyNameFormatter...)
 */
public interface ValueSetErrorCallback {
  /**
   * Fired when the bean utility failed to set a value on your bean.
   * 
   * @param bean
   *          the bean on which the value set operation was attempted
   * @param propertyName
   *          the name of the property that failed to set on the bean
   * @param value
   *          the value that the bean utility attempted to set on the bean
   * @param beanPropertyType
   *          the type of the bean property on which we tried to set the value. This may not be the same type as
   *          that of the value.
   * @param cause
   *          the reason for the failure
   * @throws ActionExecutionException
   *           throw this exception if you consider this a terminal condition
   */
  void failedToSetValue( Object bean, String propertyName, Object value, String beanPropertyType, Throwable cause )
    throws Exception;

  /**
   * Fired if, prior to the value being set, the write-check on the property fails.
   * 
   * @param bean
   *          the bean on which write test was performed
   * @param propertyName
   *          the name of the property that is not writable on the bean
   * @throws Exception
   *           throw an exception if you consider this to be a terminal condition
   */
  void propertyNotWritable( Object bean, String propertyName ) throws Exception;

}
