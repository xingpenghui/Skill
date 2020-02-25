package com.laoxing.skill.exception;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 11:53
 */
public class OrderException extends Exception{
    public OrderException(String msg){
        super(msg);
    }
    public OrderException(){
        super();
    }
}
