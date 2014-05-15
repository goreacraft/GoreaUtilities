package com.goreacraft.plugins.goreautilities;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.World;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;

public final class NBT {
    private NBT() {  
    }
    
    /**
     * Retrieve the path to the file containing information about the given player name,-
     * @param worldName - the world where the player information is located. Cannot be NULL.
     * @param playerName - the player name. Cannot be NULL.
     * @return The file to the player information. May not exist.
     */
    public static String getNBTPlayerFile(World world, String playerName) {
        Preconditions.checkNotNull("world", "world cannot be NULL");
        Preconditions.checkNotNull("playerName", "playerName cannot be NULL");
        File worldDirectory = world.getWorldFolder(); 
        
        if (!worldDirectory.exists()) 
            throw new IllegalArgumentException(
                String.format("Cannot find world %s: Directory %s doesn't exist.", world.getName(), worldDirectory));
        return new File(new File(worldDirectory, "players"), playerName + ".dat").getAbsolutePath();
    }

    /**
     * Load a NBT compound from a compressed file.
     * @param file - the file.
     * @return The compound.
     * @throws IOException Unable to load file.
     */
    public static NbtCompound loadFile(String file) throws IOException {
        FileInputStream stream = null;
        DataInputStream input = null;
        boolean swallow = true;
        
        try {
            stream = new FileInputStream(file);
            NbtCompound result = NbtBinarySerializer.DEFAULT.
                deserializeCompound(input = new DataInputStream(new GZIPInputStream(stream)));
            swallow = false;
            return result;
        } finally {
            // You can avoid this pattern in Java 7 (try-with-resources)
            if      (input != null) Closeables.close(input, swallow);
            else if (stream != null) Closeables.close(stream, swallow);
        }       
    }

    /**
     * Save a NBT compound from a compressed file.
     * @param file - the file.
     * @return The compound.
     * @throws IOException Unable to load file.
     */
    public static void saveFile(String file, NbtCompound compound) throws IOException {
        FileOutputStream stream = null;
        DataOutputStream output = null;
        boolean swallow = true;
        
        try {
            stream = new FileOutputStream(file);
            NbtBinarySerializer.DEFAULT.
                serialize(compound, output = new DataOutputStream(new GZIPOutputStream(stream)));
            swallow = false;
        } finally {
            // Note the order
            if      (output != null) Closeables.close(output, swallow);
            else if (stream != null) Closeables.close(stream, swallow);
        }       
    }
}