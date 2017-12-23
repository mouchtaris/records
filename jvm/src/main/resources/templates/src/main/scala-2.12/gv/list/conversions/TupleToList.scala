package gv
package list
package conversions

trait TupleToList<%= @n %> extends Any {

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
