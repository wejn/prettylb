! prettylb module
= What's this
This WMS 3.X module embeds inline XSL stylesheet within LoadBalancer's
`serverInfoXML` output.

That means you can still parse the XML as usual (no changes in your tools
necessary) and if you display the URL in your browser you get a nice table
instead of raw XML. And it will auto-refresh every 20 seconds.

It's based on source file `HTTPLoadBalancerServerInfoXML.java` from
"LoadBalancer_2.0.zip"<http://www.wowzamedia.com/downloads/forums/loadbalancer/LoadBalancer_2.0.zip>
package provided by "Wowza Media Systems, LLC"<http://www.wowza.com/>.

= Installation
Add `wejn-prettylb.jar` to Wowza's `lib/` directory.

Add following to `HTTPProviders` section of your `VHost.xml` where you
would normally load the
`com.wowza.wms.plugin.loadbalancer.HTTPLoadBalancerServerInfoXML`
HTTP provider:

{{{xml
<HTTPProvider> 
	<BaseClass>cz.wejn.prettylb.PrettyServerInfoXML</BaseClass>      
	<RequestFilters>*prettysinfoxml</RequestFilters>
	<AuthenticationMethod>none</AuthenticationMethod>
</HTTPProvider>
}}}

Feel free to adjust `RequestFilters` and `AuthenticationMethod` to suit
your needs.

Restart Wowza to finish installation.

Then request URL similar to `http://your.wowza:8086/prettysinfoxml`.

= Configuration options
None.

= JMX interface
None.

= License
Copyright (c) 2013 Michal "Wejn" Jirku <box@wejn.org>

This work is licensed under the Creative Commons Attribution 3.0 Czech Republic License. To view a copy of this license, visit "http://creativecommons.org/licenses/by/3.0/cz/"<http://creativecommons.org/licenses/by/3.0/cz/>.

That is, as long as the original license of HTTPLoadBalancerServerInfoXML.java
permits such licensing terms.
