use jni::{
    objects::{JByteBuffer, JClass},
    JNIEnv
};

use crate::{
    reader::Reader,
    types::{Import, PrimitiveType}
};

pub mod codegen;
pub mod reader;
pub mod types;

#[allow(non_snake_case)]
#[no_mangle]
pub extern "system" fn Java_com_projectkml_jinvoke_JInvoke_generateModule(env: JNIEnv, _class: JClass, buffer: JByteBuffer) {
    let buffer = env.get_direct_buffer_address(buffer).unwrap();

    let mut imports = Vec::new();

    let mut reader = Reader::new(buffer);

    while reader.has_bytes() {
        let java_name = reader.try_read_string().unwrap();
        let name = reader.try_read_string().unwrap();

        let return_type = reader.try_read_primitive().unwrap();

        let num_param_types = reader.try_read_i32().unwrap() as usize;
        let param_types: Vec<_> = (0..num_param_types).into_iter().map(|_| reader.try_read_primitive().unwrap()).collect();

        imports.push(Import {
            java_name,
            name,
            return_type,
            param_types
        });
    }

    println!("{:#?}", imports);
}
