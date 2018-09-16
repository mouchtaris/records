#[derive(Debug)]
struct User {
    id: String,
}

#[derive(Debug)]
struct Users {
    users: std::vec::Vec<User>,
}
impl Users {
    fn new() -> Users {
        Users {
            users: vec!(
                User { id: String::from("isi") },
            ),
        }
    }
}
fn main() {
    println!("User1: {:?}", Users::new());
}