package org.realityforge.gwt.appcache.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.realityforge.gwt.appcache.server.BindingProperty;
import org.realityforge.gwt.appcache.server.Permutation;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class AppcacheLinkerTest
{
  @Test
  public void getConfigurationValues()
  {
    final AppcacheLinker linker = new AppcacheLinker();
    final TreeSet<ConfigurationProperty> properties = new TreeSet<ConfigurationProperty>();
    properties.add( new TestConfigurationProperty( "ba", new ArrayList<String>() ) );
    properties.add( new TestConfigurationProperty( "foo", Arrays.asList( "V1", "V2" ) ) );
    final LinkerContext context = mock( LinkerContext.class );
    when( context.getConfigurationProperties() ).thenReturn( properties );
    final Set<String> values = linker.getConfigurationValues( context, "foo" );
    assertTrue( values.contains( "V1" ) );
    assertTrue( values.contains( "V2" ) );
  }

  @Test
  public void joinValues()
  {
    final AppcacheLinker linker = new AppcacheLinker();
    final TreeSet<String> strings = new TreeSet<String>();
    assertEquals( linker.joinValues( strings ), "" );
    strings.add( "a" );
    assertEquals( linker.joinValues( strings ), "a" );
    strings.add( "b" );
    strings.add( "c" );
    assertEquals( linker.joinValues( strings ), "a,b,c" );
  }

  @Test
  public void collectValuesForKey()
  {
    final AppcacheLinker linker = new AppcacheLinker();
    final TreeMap<Integer, Set<BindingProperty>> bindings = new TreeMap<Integer, Set<BindingProperty>>();
    final HashSet<BindingProperty> binding0 = new HashSet<BindingProperty>();
    binding0.add( new BindingProperty( "a", "z1" ) );
    binding0.add( new BindingProperty( "b", "z2" ) );
    bindings.put( 0, binding0 );
    final HashSet<String> values0 = linker.collectValuesForKey( bindings, "a" );
    assertEquals( values0.size(), 1 );
    assertTrue( values0.contains( "z1" ) );

    final HashSet<BindingProperty> binding1 = new HashSet<BindingProperty>();
    binding1.add( new BindingProperty( "a", "w1" ) );
    binding1.add( new BindingProperty( "b", "w2" ) );
    bindings.put( 1, binding1 );

    final HashSet<String> values1 = linker.collectValuesForKey( bindings, "a" );
    assertEquals( values1.size(), 2 );
    assertTrue( values1.contains( "z1" ) );
    assertTrue( values1.contains( "w1" ) );
  }

  @Test
  public void writeManifest()
  {
    final AppcacheLinker linker = new AppcacheLinker();
    final HashSet<String> staticResources = new HashSet<String>();
    staticResources.add( "index.html" );
    final HashSet<String> cacheResources = new HashSet<String>();
    staticResources.add( "5435435435435435FDEC.js" );
    final String manifest = linker.writeManifest( staticResources, cacheResources );
    final String[] lines = manifest.split( "\n" );
    assertEquals( lines[ 0 ], "CACHE MANIFEST" );

    final int cacheSectionStart = findLine( lines, 0, lines.length, "CACHE:" );
    final int networkSectionStart = findLine( lines, cacheSectionStart + 1, lines.length, "NETWORK:" );

    assertNotEquals( findLine( lines, networkSectionStart + 1, lines.length, "*" ), -1 );
    assertNotEquals( findLine( lines, cacheSectionStart + 1, networkSectionStart, "index.html" ), -1 );
    assertNotEquals( findLine( lines, cacheSectionStart + 1, networkSectionStart, "5435435435435435FDEC.js" ), -1 );
  }

  private int findLine( final String[] lines, final int start, final int end, final String line )
  {
    for ( int i = start; i < end; i++ )
    {
      if ( lines[ i ].equals( line ) )
      {
        return i;
      }
    }
    return -1;
  }
}