package pw.fusionmine.fusionblocks.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

public class MinecraftColor {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:((?:#[0-9a-fA-F]{6}:?)+)>(.*?)</gradient>");

    public static Color getColorByName(String name) {
        String var1 = name.toLowerCase();
        byte var2 = -1;
        switch(var1.hashCode()) {
            case -1081301904:
                if (var1.equals("maroon")) {
                    var2 = 5;
                }
                break;
            case -1008851410:
                if (var1.equals("orange")) {
                    var2 = 16;
                }
                break;
            case -976943172:
                if (var1.equals("purple")) {
                    var2 = 15;
                }
                break;
            case -902311155:
                if (var1.equals("silver")) {
                    var2 = 1;
                }
                break;
            case -734239628:
                if (var1.equals("yellow")) {
                    var2 = 6;
                }
                break;
            case -519653673:
                if (var1.equals("fuchsia")) {
                    var2 = 14;
                }
                break;
            case 112785:
                if (var1.equals("red")) {
                    var2 = 4;
                }
                break;
            case 3002044:
                if (var1.equals("aqua")) {
                    var2 = 10;
                }
                break;
            case 3027034:
                if (var1.equals("blue")) {
                    var2 = 12;
                }
                break;
            case 3181155:
                if (var1.equals("gray")) {
                    var2 = 2;
                }
                break;
            case 3321813:
                if (var1.equals("lime")) {
                    var2 = 8;
                }
                break;
            case 3374006:
                if (var1.equals("navy")) {
                    var2 = 13;
                }
                break;
            case 3555932:
                if (var1.equals("teal")) {
                    var2 = 11;
                }
                break;
            case 93818879:
                if (var1.equals("black")) {
                    var2 = 3;
                }
                break;
            case 98619139:
                if (var1.equals("green")) {
                    var2 = 9;
                }
                break;
            case 105832923:
                if (var1.equals("olive")) {
                    var2 = 7;
                }
                break;
            case 113101865:
                if (var1.equals("white")) {
                    var2 = 0;
                }
        }

        switch(var2) {
            case 0:
                return Color.WHITE;
            case 1:
                return Color.SILVER;
            case 2:
                return Color.GRAY;
            case 3:
                return Color.BLACK;
            case 4:
                return Color.RED;
            case 5:
                return Color.MAROON;
            case 6:
                return Color.YELLOW;
            case 7:
                return Color.OLIVE;
            case 8:
                return Color.LIME;
            case 9:
                return Color.GREEN;
            case 10:
                return Color.AQUA;
            case 11:
                return Color.TEAL;
            case 12:
                return Color.BLUE;
            case 13:
                return Color.NAVY;
            case 14:
                return Color.FUCHSIA;
            case 15:
                return Color.PURPLE;
            case 16:
                return Color.ORANGE;
            default:
                return Color.WHITE;
        }
    }

    public static String format(String str) {
        return translate(parseGradient(str));
    }

    private static String translate(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            String hexColor = matcher.group();
            matcher.appendReplacement(buffer, ChatColor.of(hexColor).toString());
        }

        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    private static String parseGradient(String input) {
        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();

        while(matcher.find()) {
            String colorsGroup = matcher.group(1);
            String text = matcher.group(2);
            String[] hexColors = colorsGroup.split(":");
            String gradientText = generateMultiGradient(text, hexColors);
            matcher.appendReplacement(result, Matcher.quoteReplacement(gradientText));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static String generateMultiGradient(String text, String[] colors) {
        if (colors.length >= 2 && !text.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int parts = colors.length - 1;
            int length = text.length();
            int baseSegment = length / parts;
            int remainder = length % parts;
            int charIndex = 0;

            for(int i = 0; i < parts; ++i) {
                int segmentLength = baseSegment + (i < remainder ? 1 : 0);
                int[] startRGB = hexToRgb(colors[i].substring(1));
                int[] endRGB = hexToRgb(colors[i + 1].substring(1));

                for(int j = 0; j < segmentLength; ++j) {
                    float ratio = (float)j / (float)Math.max(segmentLength - 1, 1);
                    int red = (int)((float)startRGB[0] + ratio * (float)(endRGB[0] - startRGB[0]));
                    int green = (int)((float)startRGB[1] + ratio * (float)(endRGB[1] - startRGB[1]));
                    int blue = (int)((float)startRGB[2] + ratio * (float)(endRGB[2] - startRGB[2]));
                    String hex = String.format("#%02x%02x%02x", red, green, blue);
                    builder.append(ChatColor.of(hex)).append(text.charAt(charIndex++));
                }
            }

            return builder.toString();
        } else {
            return text;
        }
    }

    private static int[] hexToRgb(String hex) {
        return new int[]{Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4, 6), 16)};
    }

}