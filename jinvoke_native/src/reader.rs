use std::{mem, slice, str::Utf8Error};

use thiserror::Error;

use crate::PrimitiveType;

#[derive(Error, Debug)]
pub enum ReaderError {
    #[error("there was an overflow while reading")]
    Overflow,
    #[error("Invalid utf8")]
    InvalidUtf8(#[from] Utf8Error)
}

pub type ReaderResult<T> = Result<T, ReaderError>;

pub struct Reader<'a> {
    data: &'a [u8],
    offset: usize
}

macro_rules! impl_read_primitive {
    ($primitive_type: ty, $name: ident) => {
        pub fn $name(&mut self) -> ReaderResult<$primitive_type> {
            if self.has_bytes_len(mem::size_of::<$primitive_type>()) {
                unsafe {
                    let value = *((self.data.as_ptr().offset(self.offset as isize)) as *const $primitive_type);
                    self.offset += mem::size_of::<$primitive_type>();
                    Ok(value)
                }
            } else {
                Err(ReaderError::Overflow)
            }
        }
    };
}

impl<'a> Reader<'a> {
    #[inline]
    pub fn new(data: &'a [u8]) -> Self {
        Self { data, offset: 0 }
    }

    pub fn has_bytes(&self) -> bool {
        self.has_bytes_len(1)
    }

    fn has_bytes_len(&self, len: usize) -> bool {
        self.offset + len <= self.data.len()
    }

    impl_read_primitive!(u8, try_read_u8);
    impl_read_primitive!(i32, try_read_i32);

    pub fn try_read_primitive(&mut self) -> ReaderResult<PrimitiveType> {
        Ok(unsafe { mem::transmute(self.try_read_u8()?) })
    }

    pub fn try_read_string(&mut self) -> ReaderResult<String> {
        let size = self.try_read_i32()? as usize;

        if self.has_bytes_len(size) {
            let string = String::from(std::str::from_utf8(unsafe { slice::from_raw_parts(self.data.as_ptr().offset(self.offset as isize), size) })?);
            self.offset += size;

            Ok(string)
        } else {
            Err(ReaderError::Overflow)
        }
    }
}
