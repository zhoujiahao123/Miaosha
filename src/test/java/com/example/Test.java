package com.example;

public class Test {
    public static void main(String[] args) {
        System.out.println(fun());
    }
    public static String fun(){
        try {
            return "try";
        }finally {
            return "finally";
        }
    }
}
