package gv
package list
package conversions

trait TupleConversions<%= @n %> {

  final type TupleType = <%= @tuple_type %>
  final type ListType = <%= @list_type %>

  final object Tuple<%= @n %>ToList extends ToList[TupleType, ListType] {

    def apply(t: TupleType): ListType =
      <% (n..1).each do |i| %>
      t._%<= i %> ::
      <% end %> ::
      Nil

  }

}
