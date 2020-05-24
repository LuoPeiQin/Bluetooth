/*
 * Copyright (c) 2019. stag All rights reserved.
 */

package com.luo.bluetooth.encryption;


import com.stag.bluetooth.util.ByteUtils;

/**
 * AES加密
 */
public class Aes {

    // foreward sbox
    private static final byte sbox[] = {
            //0          1            2           3           4            5           6            7           8            9            A           B            C           D           E            F
            (byte) 0x63, (byte) 0x7c, (byte) 0x77, (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5, (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab, (byte) 0x76, //0
            (byte) 0xca, (byte) 0x82, (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47, (byte) 0xf0, (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, (byte) 0x72, (byte) 0xc0, //1
            (byte) 0xb7, (byte) 0xfd, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3f, (byte) 0xf7, (byte) 0xcc, (byte) 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31, (byte) 0x15, //2
            (byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9a, (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2, (byte) 0x75, //3
            (byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a, (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0, (byte) 0x52, (byte) 0x3b, (byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3, (byte) 0x2f, (byte) 0x84, //4
            (byte) 0x53, (byte) 0xd1, (byte) 0x00, (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b, (byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a, (byte) 0x4c, (byte) 0x58, (byte) 0xcf, //5
            (byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33, (byte) 0x85, (byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f, (byte) 0x50, (byte) 0x3c, (byte) 0x9f, (byte) 0xa8, //6
            (byte) 0x51, (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, (byte) 0x38, (byte) 0xf5, (byte) 0xbc, (byte) 0xb6, (byte) 0xda, (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2, //7
            (byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f, (byte) 0x97, (byte) 0x44, (byte) 0x17, (byte) 0xc4, (byte) 0xa7, (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19, (byte) 0x73, //8
            (byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc, (byte) 0x22, (byte) 0x2a, (byte) 0x90, (byte) 0x88, (byte) 0x46, (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e, (byte) 0x0b, (byte) 0xdb, //9
            (byte) 0xe0, (byte) 0x32, (byte) 0x3a, (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c, (byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4, (byte) 0x79, //A
            (byte) 0xe7, (byte) 0xc8, (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e, (byte) 0xa9, (byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea, (byte) 0x65, (byte) 0x7a, (byte) 0xae, (byte) 0x08, //B
            (byte) 0xba, (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6, (byte) 0xe8, (byte) 0xdd, (byte) 0x74, (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a, //C
            (byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xf6, (byte) 0x0e, (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d, (byte) 0x9e, //D
            (byte) 0xe1, (byte) 0xf8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94, (byte) 0x9b, (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55, (byte) 0x28, (byte) 0xdf, //E
            (byte) 0x8c, (byte) 0xa1, (byte) 0x89, (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68, (byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0, (byte) 0x54, (byte) 0xbb, (byte) 0x16}; //F
    // inverse sbox
    private static final byte rsbox[] = {
            (byte) 0x52, (byte) 0x09, (byte) 0x6a, (byte) 0xd5, (byte) 0x30, (byte) 0x36, (byte) 0xa5, (byte) 0x38, (byte) 0xbf, (byte) 0x40, (byte) 0xa3, (byte) 0x9e, (byte) 0x81, (byte) 0xf3, (byte) 0xd7, (byte) 0xfb,
            (byte) 0x7c, (byte) 0xe3, (byte) 0x39, (byte) 0x82, (byte) 0x9b, (byte) 0x2f, (byte) 0xff, (byte) 0x87, (byte) 0x34, (byte) 0x8e, (byte) 0x43, (byte) 0x44, (byte) 0xc4, (byte) 0xde, (byte) 0xe9, (byte) 0xcb,
            (byte) 0x54, (byte) 0x7b, (byte) 0x94, (byte) 0x32, (byte) 0xa6, (byte) 0xc2, (byte) 0x23, (byte) 0x3d, (byte) 0xee, (byte) 0x4c, (byte) 0x95, (byte) 0x0b, (byte) 0x42, (byte) 0xfa, (byte) 0xc3, (byte) 0x4e,
            (byte) 0x08, (byte) 0x2e, (byte) 0xa1, (byte) 0x66, (byte) 0x28, (byte) 0xd9, (byte) 0x24, (byte) 0xb2, (byte) 0x76, (byte) 0x5b, (byte) 0xa2, (byte) 0x49, (byte) 0x6d, (byte) 0x8b, (byte) 0xd1, (byte) 0x25,
            (byte) 0x72, (byte) 0xf8, (byte) 0xf6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16, (byte) 0xd4, (byte) 0xa4, (byte) 0x5c, (byte) 0xcc, (byte) 0x5d, (byte) 0x65, (byte) 0xb6, (byte) 0x92,
            (byte) 0x6c, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xfd, (byte) 0xed, (byte) 0xb9, (byte) 0xda, (byte) 0x5e, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xa7, (byte) 0x8d, (byte) 0x9d, (byte) 0x84,
            (byte) 0x90, (byte) 0xd8, (byte) 0xab, (byte) 0x00, (byte) 0x8c, (byte) 0xbc, (byte) 0xd3, (byte) 0x0a, (byte) 0xf7, (byte) 0xe4, (byte) 0x58, (byte) 0x05, (byte) 0xb8, (byte) 0xb3, (byte) 0x45, (byte) 0x06,
            (byte) 0xd0, (byte) 0x2c, (byte) 0x1e, (byte) 0x8f, (byte) 0xca, (byte) 0x3f, (byte) 0x0f, (byte) 0x02, (byte) 0xc1, (byte) 0xaf, (byte) 0xbd, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8a, (byte) 0x6b,
            (byte) 0x3a, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4f, (byte) 0x67, (byte) 0xdc, (byte) 0xea, (byte) 0x97, (byte) 0xf2, (byte) 0xcf, (byte) 0xce, (byte) 0xf0, (byte) 0xb4, (byte) 0xe6, (byte) 0x73,
            (byte) 0x96, (byte) 0xac, (byte) 0x74, (byte) 0x22, (byte) 0xe7, (byte) 0xad, (byte) 0x35, (byte) 0x85, (byte) 0xe2, (byte) 0xf9, (byte) 0x37, (byte) 0xe8, (byte) 0x1c, (byte) 0x75, (byte) 0xdf, (byte) 0x6e,
            (byte) 0x47, (byte) 0xf1, (byte) 0x1a, (byte) 0x71, (byte) 0x1d, (byte) 0x29, (byte) 0xc5, (byte) 0x89, (byte) 0x6f, (byte) 0xb7, (byte) 0x62, (byte) 0x0e, (byte) 0xaa, (byte) 0x18, (byte) 0xbe, (byte) 0x1b,
            (byte) 0xfc, (byte) 0x56, (byte) 0x3e, (byte) 0x4b, (byte) 0xc6, (byte) 0xd2, (byte) 0x79, (byte) 0x20, (byte) 0x9a, (byte) 0xdb, (byte) 0xc0, (byte) 0xfe, (byte) 0x78, (byte) 0xcd, (byte) 0x5a, (byte) 0xf4,
            (byte) 0x1f, (byte) 0xdd, (byte) 0xa8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xc7, (byte) 0x31, (byte) 0xb1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xec, (byte) 0x5f,
            (byte) 0x60, (byte) 0x51, (byte) 0x7f, (byte) 0xa9, (byte) 0x19, (byte) 0xb5, (byte) 0x4a, (byte) 0x0d, (byte) 0x2d, (byte) 0xe5, (byte) 0x7a, (byte) 0x9f, (byte) 0x93, (byte) 0xc9, (byte) 0x9c, (byte) 0xef,
            (byte) 0xa0, (byte) 0xe0, (byte) 0x3b, (byte) 0x4d, (byte) 0xae, (byte) 0x2a, (byte) 0xf5, (byte) 0xb0, (byte) 0xc8, (byte) 0xeb, (byte) 0xbb, (byte) 0x3c, (byte) 0x83, (byte) 0x53, (byte) 0x99, (byte) 0x61,
            (byte) 0x17, (byte) 0x2b, (byte) 0x04, (byte) 0x7e, (byte) 0xba, (byte) 0x77, (byte) 0xd6, (byte) 0x26, (byte) 0xe1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0c, (byte) 0x7d};
    // round constant
    private static final byte Rcon[] = {
            (byte) 0x8d, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36};

    // expand the key
    private static byte[] expandKey(byte[] key) {
        byte[] expandedKey = new byte[176];
        int ii, buf1;
        for (ii = 0; ii < 16; ii++)
            expandedKey[ii] = key[ii];
        for (ii = 1; ii < 11; ii++) {
            buf1 = expandedKey[ii * 16 - 4];
            expandedKey[ii * 16 + 0] = (byte) ((sbox[expandedKey[ii * 16 - 3] & 0xff] ^ expandedKey[(ii - 1) * 16 + 0] ^ Rcon[ii]) & 0xff);
            expandedKey[ii * 16 + 1] = (byte) ((sbox[expandedKey[ii * 16 - 2] & 0xff] ^ expandedKey[(ii - 1) * 16 + 1]) & 0xff);
            expandedKey[ii * 16 + 2] = (byte) ((sbox[expandedKey[ii * 16 - 1] & 0xff] ^ expandedKey[(ii - 1) * 16 + 2]) & 0xff);
            expandedKey[ii * 16 + 3] = (byte) ((sbox[buf1 & 0xff] ^ expandedKey[(ii - 1) * 16 + 3]) & 0xff);
            expandedKey[ii * 16 + 4] = (byte) ((expandedKey[(ii - 1) * 16 + 4] ^ expandedKey[ii * 16 + 0]) & 0xff);
            expandedKey[ii * 16 + 5] = (byte) ((expandedKey[(ii - 1) * 16 + 5] ^ expandedKey[ii * 16 + 1]) & 0xff);
            expandedKey[ii * 16 + 6] = (byte) ((expandedKey[(ii - 1) * 16 + 6] ^ expandedKey[ii * 16 + 2]) & 0xff);
            expandedKey[ii * 16 + 7] = (byte) ((expandedKey[(ii - 1) * 16 + 7] ^ expandedKey[ii * 16 + 3]) & 0xff);
            expandedKey[ii * 16 + 8] = (byte) ((expandedKey[(ii - 1) * 16 + 8] ^ expandedKey[ii * 16 + 4]) & 0xff);
            expandedKey[ii * 16 + 9] = (byte) ((expandedKey[(ii - 1) * 16 + 9] ^ expandedKey[ii * 16 + 5]) & 0xff);
            expandedKey[ii * 16 + 10] = (byte) ((expandedKey[(ii - 1) * 16 + 10] ^ expandedKey[ii * 16 + 6]) & 0xff);
            expandedKey[ii * 16 + 11] = (byte) ((expandedKey[(ii - 1) * 16 + 11] ^ expandedKey[ii * 16 + 7]) & 0xff);
            expandedKey[ii * 16 + 12] = (byte) ((expandedKey[(ii - 1) * 16 + 12] ^ expandedKey[ii * 16 + 8]) & 0xff);
            expandedKey[ii * 16 + 13] = (byte) ((expandedKey[(ii - 1) * 16 + 13] ^ expandedKey[ii * 16 + 9]) & 0xff);
            expandedKey[ii * 16 + 14] = (byte) ((expandedKey[(ii - 1) * 16 + 14] ^ expandedKey[ii * 16 + 10]) & 0xff);
            expandedKey[ii * 16 + 15] = (byte) ((expandedKey[(ii - 1) * 16 + 15] ^ expandedKey[ii * 16 + 11]) & 0xff);
        }
        return expandedKey;
    }

    // multiply by 2 in the galois field
    private static byte galois_mul2(byte value) {
        if (value >> 7 != 0x00) {
            value = (byte) ((value << 1) & 0xff);
            return (byte) ((value ^ 0x1b) & 0xff);
        } else {
            return (byte) ((value << 1) & 0xff);
        }
    }

    // straight foreward aes encryption implementation
//   first the group of operations
//     - addroundkey
//     - subbytes
//     - shiftrows
//     - mixcolums
//   is executed 9 times, after this addroundkey to finish the 9th round,
//   after that the 10th round without mixcolums
//   no further subfunctions to save cycles for function calls
//   no structuring with "for (....)" to save cycles
    private static byte[] aes_encr(byte[] input, byte[] expandedKey) {
        byte buf1, buf2, buf3, round;
        byte[] state = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            state[i] = input[i];
        }
        for (round = 0; round < 9; round++) {
            // addroundkey, sbox and shiftrows
            // row 0
            state[0] = sbox[(state[0] ^ expandedKey[(round * 16)]) & 0xff];
            state[4] = sbox[(state[4] ^ expandedKey[(round * 16) + 4]) & 0xff];
            state[8] = sbox[(state[8] ^ expandedKey[(round * 16) + 8]) & 0xff];
            state[12] = sbox[(state[12] ^ expandedKey[(round * 16) + 12]) & 0xff];
            // row 1
            buf1 = (byte) ((state[1] ^ expandedKey[(round * 16) + 1]) & 0xff);
            state[1] = sbox[(state[5] ^ expandedKey[(round * 16) + 5]) & 0xff];
            state[5] = sbox[(state[9] ^ expandedKey[(round * 16) + 9]) & 0xff];
            state[9] = sbox[(state[13] ^ expandedKey[(round * 16) + 13]) & 0xff];
            state[13] = sbox[buf1 & 0xff];
            // row 2
            buf1 = (byte) ((state[2] ^ expandedKey[(round * 16) + 2]) & 0xff);
            buf2 = (byte) ((state[6] ^ expandedKey[(round * 16) + 6]) & 0xff);
            state[2] = sbox[(state[10] ^ expandedKey[(round * 16) + 10]) & 0xff];
            state[6] = sbox[(state[14] ^ expandedKey[(round * 16) + 14]) & 0xff];
            state[10] = sbox[buf1 & 0xff];
            state[14] = sbox[buf2 & 0xff];
            // row 3
            buf1 = (byte) ((state[15] ^ expandedKey[(round * 16) + 15]) & 0xff);
            state[15] = sbox[(state[11] ^ expandedKey[(round * 16) + 11]) & 0xff];
            state[11] = sbox[(state[7] ^ expandedKey[(round * 16) + 7]) & 0xff];
            state[7] = sbox[(state[3] ^ expandedKey[(round * 16) + 3]) & 0xff];
            state[3] = sbox[buf1 & 0xff];
            // mixcolums //////////
            // col1
            buf1 = (byte) ((state[0] ^ state[1] ^ state[2] ^ state[3]) & 0xff);
            buf2 = state[0];
            buf3 = (byte) ((state[0] ^ state[1]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[0] = (byte) ((state[0] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[1] ^ state[2]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[1] = (byte) ((state[1] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[2] ^ state[3]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[2] = (byte) ((state[2] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[3] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[3] = (byte) ((state[3] ^ buf3 ^ buf1) & 0xff);
            // col2
            buf1 = (byte) ((state[4] ^ state[5] ^ state[6] ^ state[7]) & 0xff);
            buf2 = state[4];
            buf3 = (byte) ((state[4] ^ state[5]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[4] = (byte) ((state[4] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[5] ^ state[6]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[5] = (byte) ((state[5] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[6] ^ state[7]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[6] = (byte) ((state[6] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[7] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[7] = (byte) ((state[7] ^ buf3 ^ buf1) & 0xff);
            // col3
            buf1 = (byte) ((state[8] ^ state[9] ^ state[10] ^ state[11]) & 0xff);
            buf2 = state[8];
            buf3 = (byte) ((state[8] ^ state[9]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[8] = (byte) ((state[8] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[9] ^ state[10]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[9] = (byte) ((state[9] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[10] ^ state[11]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[10] = (byte) ((state[10] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[11] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[11] = (byte) ((state[11] ^ buf3 ^ buf1) & 0xff);
            // col4
            buf1 = (byte) ((state[12] ^ state[13] ^ state[14] ^ state[15]) & 0xff);
            buf2 = state[12];
            buf3 = (byte) ((state[12] ^ state[13]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[12] = (byte) ((state[12] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[13] ^ state[14]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[13] = (byte) ((state[13] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[14] ^ state[15]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[14] = (byte) ((state[14] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[15] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[15] = (byte) ((state[15] ^ buf3 ^ buf1) & 0xff);
        }
        // 10th round without mixcols
        state[0] = sbox[(state[0] ^ expandedKey[(round * 16)]) & 0xff];
        state[4] = sbox[(state[4] ^ expandedKey[(round * 16) + 4]) & 0xff];
        state[8] = sbox[(state[8] ^ expandedKey[(round * 16) + 8]) & 0xff];
        state[12] = sbox[(state[12] ^ expandedKey[(round * 16) + 12]) & 0xff];
        // row 1
        buf1 = (byte) ((state[1] ^ expandedKey[(round * 16) + 1]) & 0xff);
        state[1] = sbox[(state[5] ^ expandedKey[(round * 16) + 5]) & 0xff];
        state[5] = sbox[(state[9] ^ expandedKey[(round * 16) + 9]) & 0xff];
        state[9] = sbox[(state[13] ^ expandedKey[(round * 16) + 13]) & 0xff];
        state[13] = sbox[buf1 & 0xff];
        // row 2
        buf1 = (byte) ((state[2] ^ expandedKey[(round * 16) + 2]) & 0xff);
        buf2 = (byte) ((state[6] ^ expandedKey[(round * 16) + 6]) & 0xff);
        state[2] = sbox[(state[10] ^ expandedKey[(round * 16) + 10]) & 0xff];
        state[6] = sbox[(state[14] ^ expandedKey[(round * 16) + 14]) & 0xff];
        state[10] = sbox[buf1 & 0xff];
        state[14] = sbox[buf2 & 0xff];
        // row 3
        buf1 = (byte) ((state[15] ^ expandedKey[(round * 16) + 15]) & 0xff);
        state[15] = sbox[(state[11] ^ expandedKey[(round * 16) + 11]) & 0xff];
        state[11] = sbox[(state[7] ^ expandedKey[(round * 16) + 7]) & 0xff];
        state[7] = sbox[(state[3] ^ expandedKey[(round * 16) + 3]) & 0xff];
        state[3] = sbox[buf1 & 0xff];
        // last addroundkey
        state[0] ^= expandedKey[160];
        state[1] ^= expandedKey[161];
        state[2] ^= expandedKey[162];
        state[3] ^= expandedKey[163];
        state[4] ^= expandedKey[164];
        state[5] ^= expandedKey[165];
        state[6] ^= expandedKey[166];
        state[7] ^= expandedKey[167];
        state[8] ^= expandedKey[168];
        state[9] ^= expandedKey[169];
        state[10] ^= expandedKey[170];
        state[11] ^= expandedKey[171];
        state[12] ^= expandedKey[172];
        state[13] ^= expandedKey[173];
        state[14] ^= expandedKey[174];
        state[15] ^= expandedKey[175];
        return state;
    }

    // straight foreward aes decryption implementation
//   the order of substeps is the exact reverse of decryption
//   inverse functions:
//       - addRoundKey is its own inverse
//       - rsbox is inverse of sbox
//       - rightshift instead of leftshift
//       - invMixColumns = barreto + mixColumns
//   no further subfunctions to save cycles for function calls
//   no structuring with "for (....)" to save cycles
    private static byte[] aes_decr(byte[] input, byte[] expandedKey) {
        byte buf1, buf2, buf3;
        byte round;
        round = 9;
        byte[] state = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            state[i] = input[i];
        }
        // initial addroundkey
        state[0] ^= expandedKey[160];
        state[1] ^= expandedKey[161];
        state[2] ^= expandedKey[162];
        state[3] ^= expandedKey[163];
        state[4] ^= expandedKey[164];
        state[5] ^= expandedKey[165];
        state[6] ^= expandedKey[166];
        state[7] ^= expandedKey[167];
        state[8] ^= expandedKey[168];
        state[9] ^= expandedKey[169];
        state[10] ^= expandedKey[170];
        state[11] ^= expandedKey[171];
        state[12] ^= expandedKey[172];
        state[13] ^= expandedKey[173];
        state[14] ^= expandedKey[174];
        state[15] ^= expandedKey[175];

        // 10th round without mixcols
        state[0] = (byte) ((rsbox[state[0] & 0xff] ^ expandedKey[(round * 16)]) & 0xff);
        state[4] = (byte) ((rsbox[state[4] & 0xff] ^ expandedKey[(round * 16) + 4]) & 0xff);
        state[8] = (byte) ((rsbox[state[8] & 0xff] ^ expandedKey[(round * 16) + 8]) & 0xff);
        state[12] = (byte) ((rsbox[state[12] & 0xff] ^ expandedKey[(round * 16) + 12]) & 0xff);
        // row 1
        buf1 = (byte) ((rsbox[state[13] & 0xff] ^ expandedKey[(round * 16) + 1]) & 0xff);
        state[13] = (byte) ((rsbox[state[9] & 0xff] ^ expandedKey[(round * 16) + 13]) & 0xff);
        state[9] = (byte) ((rsbox[state[5] & 0xff] ^ expandedKey[(round * 16) + 9]) & 0xff);
        state[5] = (byte) ((rsbox[state[1] & 0xff] ^ expandedKey[(round * 16) + 5]) & 0xff);
        state[1] = buf1;
        // row 2
        buf1 = (byte) ((rsbox[state[2] & 0xff] ^ expandedKey[(round * 16) + 10]) & 0xff);
        buf2 = (byte) ((rsbox[state[6] & 0xff] ^ expandedKey[(round * 16) + 14]) & 0xff);
        state[2] = (byte) ((rsbox[state[10] & 0xff] ^ expandedKey[(round * 16) + 2]) & 0xff);
        state[6] = (byte) ((rsbox[state[14] & 0xff] ^ expandedKey[(round * 16) + 6]) & 0xff);
        state[10] = buf1;
        state[14] = buf2;
        // row 3
        buf1 = (byte) ((rsbox[state[3] & 0xff] ^ expandedKey[(round * 16) + 15]) & 0xff);
        state[3] = (byte) ((rsbox[state[7] & 0xff] ^ expandedKey[(round * 16) + 3]) & 0xff);
        state[7] = (byte) ((rsbox[state[11] & 0xff] ^ expandedKey[(round * 16) + 7]) & 0xff);
        state[11] = (byte) ((rsbox[state[15] & 0xff] ^ expandedKey[(round * 16) + 11]) & 0xff);
        state[15] = buf1;

        for (round = 8; round >= 0; round--) {
            // barreto
            //col1
            buf1 = galois_mul2(galois_mul2((byte) ((state[0] ^ state[2]) & 0xff)));
            buf2 = galois_mul2(galois_mul2((byte) ((state[1] ^ state[3]) & 0xff)));
            state[0] ^= buf1;
            state[1] ^= buf2;
            state[2] ^= buf1;
            state[3] ^= buf2;
            //col2
            buf1 = galois_mul2(galois_mul2((byte) ((state[4] ^ state[6]) & 0xff)));
            buf2 = galois_mul2(galois_mul2((byte) ((state[5] ^ state[7]) & 0xff)));
            state[4] ^= buf1;
            state[5] ^= buf2;
            state[6] ^= buf1;
            state[7] ^= buf2;
            //col3
            buf1 = galois_mul2(galois_mul2((byte) ((state[8] ^ state[10]) & 0xff)));
            buf2 = galois_mul2(galois_mul2((byte) ((state[9] ^ state[11]) & 0xff)));
            state[8] ^= buf1;
            state[9] ^= buf2;
            state[10] ^= buf1;
            state[11] ^= buf2;
            //col4
            buf1 = galois_mul2(galois_mul2((byte) ((state[12] ^ state[14]) & 0xff)));
            buf2 = galois_mul2(galois_mul2((byte) ((state[13] ^ state[15]) & 0xff)));
            state[12] ^= buf1;
            state[13] ^= buf2;
            state[14] ^= buf1;
            state[15] ^= buf2;
            // mixcolums //////////
            // col1
            buf1 = (byte) ((state[0] ^ state[1] ^ state[2] ^ state[3]) & 0xff);
            buf2 = state[0];
            buf3 = (byte) ((state[0] ^ state[1]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[0] = (byte) ((state[0] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[1] ^ state[2]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[1] = (byte) ((state[1] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[2] ^ state[3]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[2] = (byte) ((state[2] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[3] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[3] = (byte) ((state[3] ^ buf3 ^ buf1) & 0xff);
            // col2
            buf1 = (byte) ((state[4] ^ state[5] ^ state[6] ^ state[7]) & 0xff);
            buf2 = state[4];
            buf3 = (byte) ((state[4] ^ state[5]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[4] = (byte) ((state[4] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[5] ^ state[6]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[5] = (byte) ((state[5] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[6] ^ state[7]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[6] = (byte) ((state[6] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[7] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[7] = (byte) ((state[7] ^ buf3 ^ buf1) & 0xff);
            // col3
            buf1 = (byte) ((state[8] ^ state[9] ^ state[10] ^ state[11]) & 0xff);
            buf2 = state[8];
            buf3 = (byte) ((state[8] ^ state[9]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[8] = (byte) ((state[8] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[9] ^ state[10]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[9] = (byte) ((state[9] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[10] ^ state[11]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[10] = (byte) ((state[10] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[11] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[11] = (byte) ((state[11] ^ buf3 ^ buf1) & 0xff);
            // col4
            buf1 = (byte) ((state[12] ^ state[13] ^ state[14] ^ state[15]) & 0xff);
            buf2 = state[12];
            buf3 = (byte) ((state[12] ^ state[13]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[12] = (byte) ((state[12] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[13] ^ state[14]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[13] = (byte) ((state[13] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[14] ^ state[15]) & 0xff);
            buf3 = galois_mul2(buf3);
            state[14] = (byte) ((state[14] ^ buf3 ^ buf1) & 0xff);
            buf3 = (byte) ((state[15] ^ buf2) & 0xff);
            buf3 = galois_mul2(buf3);
            state[15] = (byte) ((state[15] ^ buf3 ^ buf1) & 0xff);
            // addroundkey, rsbox and shiftrows
            // row 0
            state[0] = (byte) ((rsbox[state[0] & 0xff] ^ expandedKey[(round * 16)]) & 0xff);
            state[4] = (byte) ((rsbox[state[4] & 0xff] ^ expandedKey[(round * 16) + 4]) & 0xff);
            state[8] = (byte) ((rsbox[state[8] & 0xff] ^ expandedKey[(round * 16) + 8]) & 0xff);
            state[12] = (byte) ((rsbox[state[12] & 0xff] ^ expandedKey[(round * 16) + 12]) & 0xff);
            // row 1
            buf1 = (byte) ((rsbox[state[13] & 0xff] ^ expandedKey[(round * 16) + 1]) & 0xff);
            state[13] = (byte) ((rsbox[state[9] & 0xff] ^ expandedKey[(round * 16) + 13]) & 0xff);
            state[9] = (byte) ((rsbox[state[5] & 0xff] ^ expandedKey[(round * 16) + 9]) & 0xff);
            state[5] = (byte) ((rsbox[state[1] & 0xff] ^ expandedKey[(round * 16) + 5]) & 0xff);
            state[1] = buf1;
            // row 2
            buf1 = (byte) ((rsbox[state[2] & 0xff] ^ expandedKey[(round * 16) + 10]) & 0xff);
            buf2 = (byte) ((rsbox[state[6] & 0xff] ^ expandedKey[(round * 16) + 14]) & 0xff);
            state[2] = (byte) ((rsbox[state[10] & 0xff] ^ expandedKey[(round * 16) + 2]) & 0xff);
            state[6] = (byte) ((rsbox[state[14] & 0xff] ^ expandedKey[(round * 16) + 6]) & 0xff);
            state[10] = buf1;
            state[14] = buf2;
            // row 3
            buf1 = (byte) ((rsbox[state[3] & 0xff] ^ expandedKey[(round * 16) + 15]) & 0xff);
            state[3] = (byte) ((rsbox[state[7] & 0xff] ^ expandedKey[(round * 16) + 3]) & 0xff);
            state[7] = (byte) ((rsbox[state[11] & 0xff] ^ expandedKey[(round * 16) + 7]) & 0xff);
            state[11] = (byte) ((rsbox[state[15] & 0xff] ^ expandedKey[(round * 16) + 11]) & 0xff);
            state[15] = buf1;
        }
        return state;
    }

    public static byte[] decode16(byte[] data, byte[] key) {
        byte[] expandedKey = expandKey(key);
        return aes_decr(data, expandedKey);
    }

    public static byte[] encode16(byte[] data, byte[] key) {
        byte[] expandedKey = expandKey(key);
        return aes_encr(data, expandedKey);
    }

    public static byte[] decode(byte[] data, byte[] key) {
        byte[] result = new byte[0];
        for (int i = 0; i < data.length / 16; i++) {
            byte[] temp = ByteUtils.subBytes(data, i * 16, 16);
            result = ByteUtils.combineByteArray(result, decode16(temp, key));
        }
        return result;
    }

    public static byte[] encode(byte[] data, byte[] key) {
        byte[] result = new byte[0];
        for (int i = 0; i < data.length / 16; i++) {
            byte[] temp = ByteUtils.subBytes(data, i * 16, 16);
            result = ByteUtils.combineByteArray(result, encode16(temp, key));
        }
        return result;
    }
}
