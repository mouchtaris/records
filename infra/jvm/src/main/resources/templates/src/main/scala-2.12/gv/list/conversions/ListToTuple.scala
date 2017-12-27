package gv
package list
package conversions
<%

  tmps =
    lazy_plus(
      each_i.lazy.map(&:to_s).map(&'t'.method(:+))
    )

  result =
    tmps
      .map { |tmp| "#{tmp}.head" }
      .reduce { |a, b| "#{a}, #{b}" }

%>
trait ListToTuple<%= @n %> extends Any {

  final implicit def listToTuple<%= @n %>[
    <%= type_params.join(', ') %>
  ]: ToTuple[
    <%= list_type %>,
    <%= tuple_type %>,
  ] =
    t1 ⇒ {<%
      tmps.each_cons(2) do |prev, tmp| %>
      val <%= tmp %> = <%= prev %>.tail<%
      end %>

      Tuple<%= @n %>(<%
        tmps.each do |tmp| %>
        <%= tmp %>.head,<%
        end%>
      )
    }

}
