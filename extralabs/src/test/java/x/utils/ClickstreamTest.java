package x.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.Gson;

public class ClickstreamTest {

  @Test
  public void testCsv() {
    String csv = ClickStreamGenerator.getClickstreamAsCsv();
    assertNotNull(csv);
    String [] tokens = csv.split(",");
    assertEquals(8, tokens.length);
  }

  @Test
  public void testJSON() {
    String json = ClickStreamGenerator.getClickstreamAsJSON();
    assertNotNull(json);

    Gson gson = new Gson();
    ClickstreamData c = gson.fromJson(json, ClickstreamData.class);
    assertNotNull(c);
    assertNotNull(c.ip);
    assertNotNull(c.user);
  }

}
