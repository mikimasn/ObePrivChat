package net.fabricmc.example;

import net.minecraft.text.*;

import javax.annotation.Nullable;
import java.awt.*;
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
        String hexcolor = "ffffff";
        int i=0;
        for (String s : parsedmessage) {
            if(s.startsWith("#")){
                hexcolor = s.substring(1,7);
                s=s.substring(7);
            }
            if(s.contains("https://")||s.contains("http://")) {
                Text tmp = new LiteralText(s);
                ClickEvent url = new ClickEvent(ClickEvent.Action.OPEN_URL, s);
                tmp = merge(tmp, url,null);
                result.append(tmp);
            }
            else{
                ExampleMod.LOGGER.info("#"+hexcolor);
                Color color=Color.decode("#"+hexcolor);
                Style style=Style.EMPTY;
                style = style.withColor(color.getRGB());
                result.append(merge(new LiteralText(s),null,style));
            }

            i++;
            result.append(" ");


        }
        return result;
    }
    private Text merge(Text text, @Nullable ClickEvent clickEvent, @Nullable Style style){
        if(style==null)
            style = text.getStyle();
        if(clickEvent!=null)
            style = style.withClickEvent(clickEvent);
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
