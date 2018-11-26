// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: rpc.proto

package org.opennms.core.ipc.rpc.kafka.model;

public final class RpcMessageProtos {
  private RpcMessageProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface RpcMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:RpcMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string rpc_id = 1;</code>
     */
    boolean hasRpcId();
    /**
     * <code>required string rpc_id = 1;</code>
     */
    java.lang.String getRpcId();
    /**
     * <code>required string rpc_id = 1;</code>
     */
    com.google.protobuf.ByteString
        getRpcIdBytes();

    /**
     * <code>required bytes rpc_content = 2;</code>
     */
    boolean hasRpcContent();
    /**
     * <code>required bytes rpc_content = 2;</code>
     */
    com.google.protobuf.ByteString getRpcContent();

    /**
     * <code>optional string system_id = 3;</code>
     */
    boolean hasSystemId();
    /**
     * <code>optional string system_id = 3;</code>
     */
    java.lang.String getSystemId();
    /**
     * <code>optional string system_id = 3;</code>
     */
    com.google.protobuf.ByteString
        getSystemIdBytes();

    /**
     * <code>optional uint64 expiration_time = 4;</code>
     */
    boolean hasExpirationTime();
    /**
     * <code>optional uint64 expiration_time = 4;</code>
     */
    long getExpirationTime();

    /**
     * <code>optional int32 current_chunk_number = 5;</code>
     */
    boolean hasCurrentChunkNumber();
    /**
     * <code>optional int32 current_chunk_number = 5;</code>
     */
    int getCurrentChunkNumber();

    /**
     * <code>optional int32 total_chunks = 6;</code>
     */
    boolean hasTotalChunks();
    /**
     * <code>optional int32 total_chunks = 6;</code>
     */
    int getTotalChunks();
  }
  /**
   * Protobuf type {@code RpcMessage}
   */
  public static final class RpcMessage extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:RpcMessage)
      RpcMessageOrBuilder {
    // Use RpcMessage.newBuilder() to construct.
    private RpcMessage(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private RpcMessage(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final RpcMessage defaultInstance;
    public static RpcMessage getDefaultInstance() {
      return defaultInstance;
    }

    public RpcMessage getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private RpcMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              rpcId_ = bs;
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              rpcContent_ = input.readBytes();
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              systemId_ = bs;
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              expirationTime_ = input.readUInt64();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              currentChunkNumber_ = input.readInt32();
              break;
            }
            case 48: {
              bitField0_ |= 0x00000020;
              totalChunks_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.internal_static_RpcMessage_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.internal_static_RpcMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.class, org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.Builder.class);
    }

    public static com.google.protobuf.Parser<RpcMessage> PARSER =
        new com.google.protobuf.AbstractParser<RpcMessage>() {
      public RpcMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RpcMessage(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<RpcMessage> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int RPC_ID_FIELD_NUMBER = 1;
    private java.lang.Object rpcId_;
    /**
     * <code>required string rpc_id = 1;</code>
     */
    public boolean hasRpcId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string rpc_id = 1;</code>
     */
    public java.lang.String getRpcId() {
      java.lang.Object ref = rpcId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          rpcId_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string rpc_id = 1;</code>
     */
    public com.google.protobuf.ByteString
        getRpcIdBytes() {
      java.lang.Object ref = rpcId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        rpcId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int RPC_CONTENT_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString rpcContent_;
    /**
     * <code>required bytes rpc_content = 2;</code>
     */
    public boolean hasRpcContent() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required bytes rpc_content = 2;</code>
     */
    public com.google.protobuf.ByteString getRpcContent() {
      return rpcContent_;
    }

    public static final int SYSTEM_ID_FIELD_NUMBER = 3;
    private java.lang.Object systemId_;
    /**
     * <code>optional string system_id = 3;</code>
     */
    public boolean hasSystemId() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional string system_id = 3;</code>
     */
    public java.lang.String getSystemId() {
      java.lang.Object ref = systemId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          systemId_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string system_id = 3;</code>
     */
    public com.google.protobuf.ByteString
        getSystemIdBytes() {
      java.lang.Object ref = systemId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        systemId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int EXPIRATION_TIME_FIELD_NUMBER = 4;
    private long expirationTime_;
    /**
     * <code>optional uint64 expiration_time = 4;</code>
     */
    public boolean hasExpirationTime() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional uint64 expiration_time = 4;</code>
     */
    public long getExpirationTime() {
      return expirationTime_;
    }

    public static final int CURRENT_CHUNK_NUMBER_FIELD_NUMBER = 5;
    private int currentChunkNumber_;
    /**
     * <code>optional int32 current_chunk_number = 5;</code>
     */
    public boolean hasCurrentChunkNumber() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>optional int32 current_chunk_number = 5;</code>
     */
    public int getCurrentChunkNumber() {
      return currentChunkNumber_;
    }

    public static final int TOTAL_CHUNKS_FIELD_NUMBER = 6;
    private int totalChunks_;
    /**
     * <code>optional int32 total_chunks = 6;</code>
     */
    public boolean hasTotalChunks() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }
    /**
     * <code>optional int32 total_chunks = 6;</code>
     */
    public int getTotalChunks() {
      return totalChunks_;
    }

    private void initFields() {
      rpcId_ = "";
      rpcContent_ = com.google.protobuf.ByteString.EMPTY;
      systemId_ = "";
      expirationTime_ = 0L;
      currentChunkNumber_ = 0;
      totalChunks_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasRpcId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasRpcContent()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getRpcIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, rpcContent_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getSystemIdBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeUInt64(4, expirationTime_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeInt32(5, currentChunkNumber_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeInt32(6, totalChunks_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getRpcIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, rpcContent_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getSystemIdBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(4, expirationTime_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, currentChunkNumber_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, totalChunks_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code RpcMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:RpcMessage)
        org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.internal_static_RpcMessage_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.internal_static_RpcMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.class, org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.Builder.class);
      }

      // Construct using org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        rpcId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        rpcContent_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        systemId_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        expirationTime_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000008);
        currentChunkNumber_ = 0;
        bitField0_ = (bitField0_ & ~0x00000010);
        totalChunks_ = 0;
        bitField0_ = (bitField0_ & ~0x00000020);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.internal_static_RpcMessage_descriptor;
      }

      public org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage getDefaultInstanceForType() {
        return org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.getDefaultInstance();
      }

      public org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage build() {
        org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage buildPartial() {
        org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage result = new org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.rpcId_ = rpcId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.rpcContent_ = rpcContent_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.systemId_ = systemId_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.expirationTime_ = expirationTime_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.currentChunkNumber_ = currentChunkNumber_;
        if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
          to_bitField0_ |= 0x00000020;
        }
        result.totalChunks_ = totalChunks_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage) {
          return mergeFrom((org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage other) {
        if (other == org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage.getDefaultInstance()) return this;
        if (other.hasRpcId()) {
          bitField0_ |= 0x00000001;
          rpcId_ = other.rpcId_;
          onChanged();
        }
        if (other.hasRpcContent()) {
          setRpcContent(other.getRpcContent());
        }
        if (other.hasSystemId()) {
          bitField0_ |= 0x00000004;
          systemId_ = other.systemId_;
          onChanged();
        }
        if (other.hasExpirationTime()) {
          setExpirationTime(other.getExpirationTime());
        }
        if (other.hasCurrentChunkNumber()) {
          setCurrentChunkNumber(other.getCurrentChunkNumber());
        }
        if (other.hasTotalChunks()) {
          setTotalChunks(other.getTotalChunks());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasRpcId()) {
          
          return false;
        }
        if (!hasRpcContent()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.opennms.core.ipc.rpc.kafka.model.RpcMessageProtos.RpcMessage) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object rpcId_ = "";
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public boolean hasRpcId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public java.lang.String getRpcId() {
        java.lang.Object ref = rpcId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            rpcId_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public com.google.protobuf.ByteString
          getRpcIdBytes() {
        java.lang.Object ref = rpcId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          rpcId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public Builder setRpcId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        rpcId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public Builder clearRpcId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        rpcId_ = getDefaultInstance().getRpcId();
        onChanged();
        return this;
      }
      /**
       * <code>required string rpc_id = 1;</code>
       */
      public Builder setRpcIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        rpcId_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString rpcContent_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes rpc_content = 2;</code>
       */
      public boolean hasRpcContent() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required bytes rpc_content = 2;</code>
       */
      public com.google.protobuf.ByteString getRpcContent() {
        return rpcContent_;
      }
      /**
       * <code>required bytes rpc_content = 2;</code>
       */
      public Builder setRpcContent(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        rpcContent_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes rpc_content = 2;</code>
       */
      public Builder clearRpcContent() {
        bitField0_ = (bitField0_ & ~0x00000002);
        rpcContent_ = getDefaultInstance().getRpcContent();
        onChanged();
        return this;
      }

      private java.lang.Object systemId_ = "";
      /**
       * <code>optional string system_id = 3;</code>
       */
      public boolean hasSystemId() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional string system_id = 3;</code>
       */
      public java.lang.String getSystemId() {
        java.lang.Object ref = systemId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            systemId_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string system_id = 3;</code>
       */
      public com.google.protobuf.ByteString
          getSystemIdBytes() {
        java.lang.Object ref = systemId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          systemId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string system_id = 3;</code>
       */
      public Builder setSystemId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        systemId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string system_id = 3;</code>
       */
      public Builder clearSystemId() {
        bitField0_ = (bitField0_ & ~0x00000004);
        systemId_ = getDefaultInstance().getSystemId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string system_id = 3;</code>
       */
      public Builder setSystemIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        systemId_ = value;
        onChanged();
        return this;
      }

      private long expirationTime_ ;
      /**
       * <code>optional uint64 expiration_time = 4;</code>
       */
      public boolean hasExpirationTime() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional uint64 expiration_time = 4;</code>
       */
      public long getExpirationTime() {
        return expirationTime_;
      }
      /**
       * <code>optional uint64 expiration_time = 4;</code>
       */
      public Builder setExpirationTime(long value) {
        bitField0_ |= 0x00000008;
        expirationTime_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint64 expiration_time = 4;</code>
       */
      public Builder clearExpirationTime() {
        bitField0_ = (bitField0_ & ~0x00000008);
        expirationTime_ = 0L;
        onChanged();
        return this;
      }

      private int currentChunkNumber_ ;
      /**
       * <code>optional int32 current_chunk_number = 5;</code>
       */
      public boolean hasCurrentChunkNumber() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>optional int32 current_chunk_number = 5;</code>
       */
      public int getCurrentChunkNumber() {
        return currentChunkNumber_;
      }
      /**
       * <code>optional int32 current_chunk_number = 5;</code>
       */
      public Builder setCurrentChunkNumber(int value) {
        bitField0_ |= 0x00000010;
        currentChunkNumber_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 current_chunk_number = 5;</code>
       */
      public Builder clearCurrentChunkNumber() {
        bitField0_ = (bitField0_ & ~0x00000010);
        currentChunkNumber_ = 0;
        onChanged();
        return this;
      }

      private int totalChunks_ ;
      /**
       * <code>optional int32 total_chunks = 6;</code>
       */
      public boolean hasTotalChunks() {
        return ((bitField0_ & 0x00000020) == 0x00000020);
      }
      /**
       * <code>optional int32 total_chunks = 6;</code>
       */
      public int getTotalChunks() {
        return totalChunks_;
      }
      /**
       * <code>optional int32 total_chunks = 6;</code>
       */
      public Builder setTotalChunks(int value) {
        bitField0_ |= 0x00000020;
        totalChunks_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 total_chunks = 6;</code>
       */
      public Builder clearTotalChunks() {
        bitField0_ = (bitField0_ & ~0x00000020);
        totalChunks_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:RpcMessage)
    }

    static {
      defaultInstance = new RpcMessage(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:RpcMessage)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RpcMessage_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_RpcMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\trpc.proto\"\221\001\n\nRpcMessage\022\016\n\006rpc_id\030\001 \002" +
      "(\t\022\023\n\013rpc_content\030\002 \002(\014\022\021\n\tsystem_id\030\003 \001" +
      "(\t\022\027\n\017expiration_time\030\004 \001(\004\022\034\n\024current_c" +
      "hunk_number\030\005 \001(\005\022\024\n\014total_chunks\030\006 \001(\005B" +
      "8\n$org.opennms.core.ipc.rpc.kafka.modelB" +
      "\020RpcMessageProtos"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_RpcMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_RpcMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_RpcMessage_descriptor,
        new java.lang.String[] { "RpcId", "RpcContent", "SystemId", "ExpirationTime", "CurrentChunkNumber", "TotalChunks", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
