package ec.com.yacare.y4all.lib.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Cipher {

  private final String password;

  public Cipher(String password) {
    this.password = password;
  }

  public byte[] encrypt(byte[] plainText) throws Exception {
    return transform(true, plainText);
  }

  public byte[] decrypt(byte[] cipherText) throws Exception {
    return transform(false, cipherText);
  }

//  private byte[] transform(boolean encrypt, byte[] inputBytes) throws Exception {
//    byte[] key = DigestUtils.md5(password.getBytes("UTF-8"));
//    BufferedBlockCipher cipher =
//        new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
//    cipher.init(encrypt, new KeyParameter(key));
//    ByteArrayInputStream input = new ByteArrayInputStream(inputBytes);
//    ByteArrayOutputStream output = new ByteArrayOutputStream();
//    int inputLen;
//    int outputLen;
//    byte[] inputBuffer = new byte[1024];
//    byte[] outputBuffer = new byte[cipher.getOutputSize(inputBuffer.length)];
//    while ((inputLen = input.read(inputBuffer)) > -1) {
//      outputLen = cipher.processBytes(inputBuffer, 0, inputLen, outputBuffer, 0);
//      if (outputLen > 0) {
//        output.write(outputBuffer, 0, outputLen);
//      }
//    }
//    outputLen = cipher.doFinal(outputBuffer, 0);
//    if (outputLen > 0) {
//      output.write(outputBuffer, 0, outputLen);
//    }
//    return output.toByteArray();
//  }

  private byte[] transform(boolean encrypt, byte[] inputBytes) throws Exception {
    byte[] key = DigestUtils.md5(password.getBytes("UTF-8"));
    BufferedBlockCipher cipher =
            new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
    cipher.init(encrypt, new KeyParameter(key));


    if (!encrypt){
      byte[] inputBytesD = Base64.decode(inputBytes);
      ByteArrayInputStream input = new ByteArrayInputStream(inputBytesD);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      int inputLen;
      int outputLen;

      byte[] inputBuffer = new byte[1024];
      byte[] outputBuffer = new byte[cipher.getOutputSize(inputBuffer.length)];
      while ((inputLen = input.read(inputBuffer)) > -1) {
        outputLen = cipher.processBytes(inputBuffer, 0, inputLen, outputBuffer, 0);
        if (outputLen > 0) {
          output.write(outputBuffer, 0, outputLen);
        }
      }
      outputLen = cipher.doFinal(outputBuffer, 0);
      return outputBuffer;
    } else {
      ByteArrayInputStream input = new ByteArrayInputStream(inputBytes);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      int inputLen;
      int outputLen;
      byte[] inputBuffer = new byte[1024];
      byte[] outputBuffer = new byte[cipher.getOutputSize(inputBuffer.length)];
      while ((inputLen = input.read(inputBuffer)) > -1) {
        outputLen = cipher.processBytes(inputBuffer, 0, inputLen, outputBuffer, 0);
        if (outputLen > 0) {
          output.write(outputBuffer, 0, outputLen);
        }
      }
      outputLen = cipher.doFinal(outputBuffer, 0);
      if (outputLen > 0) {
        output.write(outputBuffer, 0, outputLen);
      }
      byte[] base64Bytes = Base64.encode(output.toByteArray());
      return base64Bytes;
    }

  }
}
