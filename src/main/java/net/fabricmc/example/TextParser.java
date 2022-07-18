package net.fabricmc.example;

import net.minecraft.text.*;

import java.util.List;

public class TextParser {
    public Text parsemessage(String message){

        String[] parsedmessage = message.split(" ");
        BaseText result = new BaseText() {
            @Override
            public BaseText copy() {
                return null;
            }
        };
        int i=0;
        for (String s : parsedmessage) {
            if(s.contains("https://")||s.contains("http://")) {
                Text tmp = new LiteralText(s);
                ClickEvent url = new ClickEvent(ClickEvent.Action.OPEN_URL, s);
                tmp = merge(tmp, url);
                result.append(tmp);
            }
            else
               result.append(new LiteralText(s));
            i++;
            result.append(" ");


        }
        return result;
    }
    private Text merge(Text text, ClickEvent clickEvent){
        Style style = text.getStyle().withClickEvent(clickEvent);
        List<Text> tmp = text.getWithStyle(style);
        BaseText result = new BaseText() {
            @Override
            public BaseText copy() {
                return null;
            }
        };
        for (Text text1 : tmp) {
            result.append(text1);
        }
        return result;
    }
}
