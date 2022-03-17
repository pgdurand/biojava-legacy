package org.biojava.bio.seq.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;

import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

@SuppressWarnings("deprecation")
/**
 * JUnit test to check change in Uniprot data format (Feature Table) introduce in release 2019_12
 * @author Patrick G. Durand
 * @since 1.9 legacy
 */
public class UniprotFormatTest extends TestCase {
  // change in Uniprot data format (Feature Table) introduce in release 2019_12
  // see https://www.uniprot.org/news/2019/12/18/release#text%5Fft

  private static final boolean DISPLAY_ID = false;
  
  private static HashSet<String> featBiojavaLocRef;
    
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    featBiojavaLocRef = new HashSet<String>();
    featBiojavaLocRef.add("chain-1-129-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("mod_res-1-1-/IsoformId=\"P68250-2\" /note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("chain-2 147 483 647-245-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("chain-31-137-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("chain-1-55-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("transit-1--2 147 483 648-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("chain-69-255-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("chain-1-24-/note=\"toto\" /evidence=\"titi\"");
    featBiojavaLocRef.add("signal-1--2 147 483 648-/note=\"toto\" /evidence=\"titi\"");
  }

  @After
  public void tearDown() throws Exception {
  }
  public static final MessageFormat FEATURE_FORMAT = new MessageFormat("{0}-{1}-{2}-{3}");
  private String featureToString(Feature feat) {
    String f = FEATURE_FORMAT.format(new Object[] { 
        feat.getType().toLowerCase(),
        feat.getLocation().getMin(),
        feat.getLocation().getMax(),
        feat.getAnnotation().getProperty("swissprot.featureattribute").toString().trim()});
    //System.out.println("  " + f);
    return f;
  }


  @SuppressWarnings("rawtypes")
  @Test
  public void testAllLocationTypes() {
    //specific test to check all types of Uniprot FT locations
    //System.out.println(">> testUniqueFile");
    InputStream inputS = this.getClass().getResourceAsStream("/locations-up-test.dat");
    assertNotNull(inputS);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputS))) {
      HashSet<String> featBiojava;
      Sequence seq;
      // read the Uniprot File
      SequenceIterator iter = SeqIOTools.readSwissprot(reader);
      while(iter.hasNext()) {
        seq = iter.nextSequence();
        /**/

        assertNotNull(seq);
        if (DISPLAY_ID) {
          System.out.println("> " + seq.getName());
        }
        Iterator iterF = seq.features();
        featBiojava = new HashSet<String>();
        while (iterF.hasNext()) {
          Feature feat = (Feature) iterF.next();
            //BeeDeeM does not handle fuzzy locations
            featBiojava.add(featureToString(feat));
        }
        assertEquals(featBiojava.size(), 9);
        for (String str : featBiojavaLocRef) {
          featBiojava.remove(str);
        }
        assertEquals(featBiojava.size(), 0);
      }
    } catch (Exception ex) {
      fail("unable to read UniProt file: " + ex);
    }
  }
}
