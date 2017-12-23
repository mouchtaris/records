package gv
package list
package conversions

trait TupleConversions<%= @n %> {

  final implicit def tuple<%= @n %>ToList[
    <%= type_params.join(', ') %>
  ]: ToList[
    <%= tuple_type %>,
    <%= list_type %>,
  ] =
    tuple ⇒
      <% each_i do |i| %>tuple._<%= i %> ::
      <% end %>Nil

}
