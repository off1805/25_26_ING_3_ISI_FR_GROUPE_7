package com.projetTransversalIsi.security.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordGeneratorImpl  implements PasswordGenerator{

    private static final String UPPER_LETTER= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_LETTER= "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS= "0123456789";
    private static final String SYMBOLS= "!@#$%^&*()_+=-";

    private static final String ALL= UPPER_LETTER+LOWER_LETTER+DIGITS+SYMBOLS;

    private static final SecureRandom RANDOM= new SecureRandom();

    @Override
    public  String generate(){
        List<Character> password= new ArrayList<>();

        password.add(UPPER_LETTER.charAt(RANDOM.nextInt(UPPER_LETTER.length())));
        password.add(LOWER_LETTER.charAt(RANDOM.nextInt(LOWER_LETTER.length())));
        password.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.add(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));

        for(int i=0;i<8;i++){
            password.add(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        Collections.shuffle(password,RANDOM);
        StringBuilder result= new StringBuilder();
        password.forEach(result::append);
        return  result.toString();

    }
}
