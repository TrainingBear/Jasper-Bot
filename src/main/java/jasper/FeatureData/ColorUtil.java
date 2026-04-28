package jasper.featureData;

import org.jetbrains.annotations.Nullable;

import net.dv8tion.jda.api.EmbedBuilder;

public enum ColorUtil {
    NORMAL(0xff00b6),
    ERROR(0xab00ff),
    TRUE(0xff00f9),
    FALSE(0xff0089),
    TIMEOUT(0xab5291);

    private final int color;

    ColorUtil(int i) {
        this.color = i;
    }

    public int getColor() {
        return this.color;
    }

    public EmbedBuilder getEmbedMessage(@Nullable String title){
        EmbedBuilder embed = new EmbedBuilder().setColor(this.color);
        if(title != null) embed.setTitle(title);
        return embed;
    }
}