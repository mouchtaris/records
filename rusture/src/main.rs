use std::marker::PhantomData as Phan;

#[derive(Copy, Clone, Debug)] struct Player();
#[derive(Copy, Clone, Debug)] struct Soldier();
#[derive(Copy, Clone, Debug)] struct Cavalerly();
#[derive(Copy, Clone, Debug)] struct Archer();

struct FightingValue<M>(u8, Phan<M>);
impl <M> From<u8> for FightingValue<M> {
    fn from(v: u8) -> FightingValue<M> {
        FightingValue(v, Phan)
    }
}
impl <M> std::ops::Deref for FightingValue<M> {
    type Target = u8;
    fn deref(&self) -> &u8 { &self.0 }
}


struct AttackMark();
type Attack = FightingValue<AttackMark>;

struct DefenceMark();
type Defence = FightingValue<DefenceMark>;

struct MoraleMark();
type Morale = FightingValue<MoraleMark>;

struct Army {
    soldiers: Vec<Soldier>,
    horses: Vec<Cavalerly>,
    archers: Vec<Archer>,
}

trait Fighting {
    fn attack(&self) -> Attack;
    fn defence(&self) -> Defence;
    fn morale(&self) -> Morale;
}
impl Fighting for Soldier {
    fn attack(&self) -> Attack { Attack::from(1u8) }
    fn defence(&self) -> Defence { Defence::from(1u8) }
    fn morale(&self) -> Morale { Morale::from(0u8) }
}
impl Fighting for Cavalerly {
    fn attack(&self) -> Attack { Attack::from(2u8) }
    fn defence(&self) -> Defence { Defence::from(1u8) }
    fn morale(&self) -> Morale { Morale::from(1u8) }
}
impl Fighting for Archer {
    fn attack(&self) -> Attack { Attack::from(2u8) }
    fn defence(&self) -> Defence { Defence::from(0u8) }
    fn morale(&self) -> Morale { Morale::from(0u8) }
}

fn sum<T: Fighting>(ss: &Vec<T>, f: &Fn(&T) -> u8) -> u8 {
    ss.iter().map(f).sum()
}

fn main() {
    let soldiers = vec![Soldier {}; 20];
    let archers = vec![Archer {}; 10];
    let horses = vec![Cavalerly {}; 5];
    let army = Army { soldiers, archers, horses };
}