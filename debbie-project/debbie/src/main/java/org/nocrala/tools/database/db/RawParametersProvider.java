package org.nocrala.tools.database.db;

public interface RawParametersProvider {

  String getSourcedir();

  String getTargetversion();

  String getDatascenario();

  String getLayeredbuild();

  String getLayeredscenario();

  String getOnbuilderror();

  String getOncleanerror();

  String getLocalproperties();

  String getDelimiter();

  String getSolodelimiter();

  String getCasesensitivedelimiter();

  String getTreatwarningas();

  String getJdbcdriverclass();

  String getJdbcurl();

  String getJdbcusername();

  String getJdbcpassword();

}
