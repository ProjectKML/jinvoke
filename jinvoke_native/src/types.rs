#[repr(u8)]
#[derive(Copy, Clone, Debug)]
pub enum PrimitiveType {
    Byte,
    Short,
    Int,
    Long,
    Float,
    Double,
    Pointer,
    Void
}

#[derive(Debug)]
pub struct Import {
    pub java_name: String,
    pub name: String,
    pub return_type: PrimitiveType,
    pub param_types: Vec<PrimitiveType>
}
