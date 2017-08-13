package jsoniter_codegen.cfg3.decoder.com.github.blutorange.translune.socket;
public class LunarMessage implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);
if (iter.readNull()) { return null; }
com.github.blutorange.translune.socket.LunarMessage obj = (existingObj == null ? new com.github.blutorange.translune.socket.LunarMessage() : (com.github.blutorange.translune.socket.LunarMessage)existingObj);
if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {
return obj;
}
com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);
boolean once = true;
while (once) {
once = false;
iter.skip();
}
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);
iter.skip();
}
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
