package jsoniter_codegen.cfg3.encoder.com.github.blutorange.translune.socket;
public class LunarMessage implements com.jsoniter.spi.Encoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{', (byte)'}');
encode_((com.github.blutorange.translune.socket.LunarMessage)obj, stream);
}
public static void encode_(com.github.blutorange.translune.socket.LunarMessage obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
}
}
