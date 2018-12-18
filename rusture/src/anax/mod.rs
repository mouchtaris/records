use std::marker::PhantomData as Phan;

#[derive(Copy, Clone, Debug)] pub struct Player();
#[derive(Copy, Clone, Debug)] pub struct Soldier();
#[derive(Copy, Clone, Debug)] pub struct Cavalerly();
#[derive(Copy, Clone, Debug)] pub struct Archer();

#[derive(Copy, Clone, Debug)]
pub struct FightingValue<M>(u8, Phan<M>);
impl <M> From<u8> for FightingValue<M> {
    fn from(v: u8) -> FightingValue<M> {
        FightingValue(v, Phan)
    }
}
impl <M> From<FightingValue<M>> for u8 {
    fn from(v: FightingValue<M>) -> u8 {
        v.0
    }
}

#[derive(Copy, Clone, Debug)]
pub struct AttackMark();
type Attack = FightingValue<AttackMark>;

#[derive(Copy, Clone, Debug)]
pub struct DefenceMark();
type Defence = FightingValue<DefenceMark>;

#[derive(Copy, Clone, Debug)]
pub struct MoraleMark();
type Morale = FightingValue<MoraleMark>;

#[derive(Debug)]
pub struct Army {
    pub soldiers: Vec<Soldier>,
    pub horses: Vec<Cavalerly>,
    pub archers: Vec<Archer>,
}

pub trait Fighter {
    fn attack(&self) -> Attack;
    fn defence(&self) -> Defence;
    fn morale(&self) -> Morale;
}

impl Fighter for Soldier {
    fn attack(&self) -> Attack { 1u8.into() }
    fn defence(&self) -> Defence { 2u8.into() }
    fn morale(&self) -> Morale { 0u8.into() }
}
impl Fighter for Cavalerly {
    fn attack(&self) -> Attack { 1u8.into() }
    fn defence(&self) -> Defence { 1u8.into() }
    fn morale(&self) -> Morale { 1u8.into() }
}
impl Fighter for Archer {
    fn attack(&self) -> Attack { 2u8.into() }
    fn defence(&self) -> Defence { 1u8.into() }
    fn morale(&self) -> Morale { 0u8.into() }
}

trait GetFightingValue {
    fn get_fighting_value<F: Fighter>(f: &F) -> Self;
}
impl GetFightingValue for Attack {
    fn get_fighting_value<F: Fighter>(f: &F) -> Attack { f.attack() }
}
impl GetFightingValue for Defence {
    fn get_fighting_value<F: Fighter>(f: &F) -> Defence { f.defence() }
}
impl GetFightingValue for Morale {
    fn get_fighting_value<F: Fighter>(f: &F) -> Morale { f.morale() }
}
impl Army {

    //
    // THe simple life
    //
    fn _sum<T: Fighter>(ss: &[T], f: &dyn Fn(&T) -> u8) -> u8 {
        ss.iter().map(f).sum()
    }

    fn _sum_with(&self, _f: &dyn Fn(&dyn Fighter) -> u8) -> u8 {
        0
    }

    //
    // The asking for trouble life
    //
    fn sum2<'us, Us, U, F>(
        units: &'us mut Us,
        f: F,
    )
        -> u8
        where
            Us: Iterator<Item=&'us U>,
            U: 'us + Fighter,
            F: Fn(&U) -> u8,
    {
        units.map(f).sum()
    }

    fn sum2_with<'us, Us, U, M>(
        units: &'us mut Us,
        _fighting_value: &M,
    )
        -> u8
        where
            Us: Iterator<Item=&'us U>,
            U: 'us + Fighter,
            FightingValue<M>: GetFightingValue,
    {
        let f: fn(&U) -> u8 = |ref u| FightingValue::<M>::get_fighting_value(*u).into();
        Self::sum2(units, &f)
    }

    fn sum_all_with<M>(
        &self,
        fighting_value: &M,
    )
        -> FightingValue<M>
        where
            FightingValue<M>: GetFightingValue,
    {
        let r =
            Self::sum2_with(&mut self.soldiers.iter(), &fighting_value) +
            Self::sum2_with(&mut self.horses.iter(), &fighting_value) +
            Self::sum2_with(&mut self.archers.iter(), &fighting_value) +
            0
        ;
        r.into()
    }

}
impl Fighter for Army {
    fn attack(&self) -> Attack { self.sum_all_with(&AttackMark()) }
    fn defence(&self) -> Defence { self.sum_all_with(&DefenceMark()) }
    fn morale(&self) -> Morale { self.sum_all_with(&MoraleMark()) }
}
