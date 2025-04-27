package de.thedead2.minecraft_tales.util.exceptions;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;


public class UnknownFontTypeException extends IOException {
    public UnknownFontTypeException(ResourceLocation fontId) {
        super("Unknown font type: " + fontId.toString());
    }
}
