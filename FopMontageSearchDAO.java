/**
 * (C) Hubino (P) Ltd.
 *
 * The program(s) herein may be used and/or copied only with the 
 * written permission of Hubino (P) Ltd. 
 * or in accordance with the terms and conditions stipulated in the 
 * agreement/contract under which the program(s) have been supplied.
 *
 * 
 */
package com.hubino.pros.backend.fop.recherche;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hubino.pros.backend.gestuser.TechDAO;
import com.hubino.pros.backend.gestuser.TronconDAO;
import com.hubino.pros.backend.gestuser.UserDAO;
import com.hubino.pros.backend.referentiel.usine.Usine;
import com.hubino.pros.backend.referentiel.usine.UsineDAO;
import com.hubino.pros.global.Constants;
import com.hubino.pros.log.LogService;

/**
 * The Class FopMontageSearchDAO.
 * 
 * @author Vijayaraja 
 */
public class FopMontageSearchDAO implements Serializable {

    /** Logger. */
    private static LogService oLogService = null;

    /*
     * Recherche des FOP A dans une vue
     * 2 requêtes (usine FOP ou usine non FOP) pour améliorer le plan
     *
     *  Modified By Vijayaraja (13/Mar/2009)
     */

    /** The Constant SEARCH_FOP_USI_FOP. */
    private static final String SEARCH_FOP_USI_FOP =
              "select FOPA_ZONE_ID, LZON_LIBELLE, FOPA_ID, FOPA_NUMERO, FOP_NAME, INDEX_IDS,          "
            + "     case                                                                              "
            + "        when instr(fam_list,',',1,6) > 0 then                                          "
            + "             substr(fam_list ,1,instr(fam_list,',',1,3))||'<BR>'                       "
            + "             ||substr(fam_list,instr(fam_list,',',1,3)+1,instr(fam_list,',',1,6)       "
            + "             -instr(fam_list,',',1,3))||'<BR>'||                                       "
            + "             substr(fam_list,instr(fam_list,',',1,6)+1)                                "
            + "             when instr(fam_list,',',1,3) > 0 then                                     "
            + "                  substr(fam_list ,1,instr(fam_list,',',1,3))||'<BR>'                  "
            + "                  ||substr(fam_list,instr(fam_list,',',1,3)+1)                         "
            + "             else fam_list end fam_list,                                               "
            + "             OPS_ERROR_FLAG                                                            "
            + " FROM (select                                                                          "
            + "    DECODE(:1,'PRO',USI.S012_TEC_ID||USI.S015_TRO_ID,FOPA_ZONE_ID)   FOPA_ZONE_ID,     "
            + "    DECODE(:2,'PRO',S015_LB,LZON_LIBELLE)                            LZON_LIBELLE,     "
            + "    FOPA_ID,                                                                           "
            + "    FOPA_NUMERO,                                                                       "
            + "    REPLACE(REPLACE(LIND_NOM,CHR(13),' '),CHR(10),' ')               FOP_NAME,         "
            + " STRAGG(distinct FOP.INDI_ID ||',')                                  INDEX_IDS,        "
            + " RTRIM(STRAGG(distinct FOP.FAFP_S004_FAM_ID   ||','),',')            FAM_LIST,         "
            + "   MAX(OPS_ERROR_FLAG) OPS_ERROR_FLAG                                             "
            + "from                                                                                   "
            + "       (select                                                                         "
            + "            FOPA_ID,                                                                   "
            + "            FAFP_S004_FAM_ID,                                                          "
            + "            FOPA_ZONE_ID,                                                              "
            + "            FOPA_ZONE_ID||FOPA_SEQUENCE||FOPA_PROXIMITE||FOPA_DIVERSITE FOPA_NUMERO,   "
            + "            IND.INDI_ID INDI_ID,                                                       "
            + "            LIND_NOM,                                                                   "
            + "            DERN.OPS_ERROR_FLAG                                                        "
            + "        from                                                                           "
            + "            PPM_FOP_A,                                                                 "
            + "            PPM_FOP_A_INDICEES IND,                                                    "
            + "            PPM_FAMILLES_FOP_A,                                                        "
            + "            (select                                                                    "
            + "                INDI_FOPA_ID,                                                          "
            + "                max(INDI_ID) INDI_ID,                                                  "
            + "                max(INDI_FLAG_OPSERROR) OPS_ERROR_FLAG                                 "
            + "             from                                                                      "
            + "                PPM_FOP_A_INDICEES,                                                    "
            + "                PPM_FAMILLES_FOP_A                                                     "
            + "             where                                                                     "
            + "               INDI_S015_S016_USI_ID = :3                                              "
            + "               and INDI_ETAT         <> 'FIG'                                          "
            + "               and FAFP_INDI_ID       = INDI_ID                                        "
            + "               and FAFP_S004_FAM_ID   = :4                                             "
            + "             group by                                                                  "
            + "                INDI_FOPA_ID) DERN,                                                    "
            + "            PPM_LIBELLES_FOP_A                                                         "
            + "        where                                                                          "
            + "                IND.INDI_FOPA_ID = NVL(:5,IND.INDI_FOPA_ID)                            "
            + "            and FAFP_INDI_ID          = IND.INDI_ID                                    "
            + "            and FOPA_ZONE_ID           = NVL(:6,FOPA_ZONE_ID)                          "
            + "            and FOPA_METI_CODE         = 'MON'                                         "
            + "            and IND.INDI_ID            = NVL(:7,IND.INDI_ID)                           "
            + "            and IND.INDI_FOPA_ID       = FOPA_ID                                       "
            + "            and (IND.INDI_UTIL_IPN     = NVL(:8,IND.INDI_UTIL_IPN)                     "
            + "                 or IND.INDI_REDACTEUR = NVL(:9,IND.INDI_REDACTEUR))                   "
            + "            and ( (:10 = 'TOUS')                                                       "
            + "                   or (:11= 'CSR/PANIMMO'                                              "
            + "                       and (IND.INDI_FLAG_CSR='O' or IND.INDI_FLAG_PANIMO ='O'))       "
            + "                   or (:12 = 'CSR'     and IND.INDI_FLAG_CSR='O')                      "
            + "                   or (:13 = 'PANIMMO' and IND.INDI_FLAG_PANIMO ='O'))                 "
            + "            and IND.INDI_ETAT        = NVL(:14, IND.INDI_ETAT)                         "
            + "            and DERN.INDI_FOPA_ID    = IND.INDI_FOPA_ID                                "
            + "            and LIND_INDI_ID     (+) = DERN.INDI_ID                                    "
            + "            and LIND_S005_LAN_ID (+) = :15)                                            "
            + "    FOP,                                                                               "
            + "       (select                                                                         "
            + "            INDI_ID               INDI_ID,                                             "
            + "            INDI_S015_S016_USI_ID S016_USI_ID,                                         "
            + "            FAFP_S015_S012_TEC_ID S012_TEC_ID,                                         "
            + "            FAFP_S015_TRO_ID      S015_TRO_ID,                                         "
            + "            FAFP_S004_FAM_ID FAM                                                       "
            + "        from                                                                           "
            + "            PPM_FOP_A_INDICEES,                                                        "
            + "            PPM_FAMILLES_FOP_A                                                         "
            + "        where                                                                          "
            + "            INDI_S015_S016_USI_ID     = :16                                            "
            + "            and FAFP_S004_FAM_ID      = :17                                            "
            + "            and FAFP_INDI_ID           = INDI_ID                                       "
            + "            and FAFP_S015_S012_TEC_ID = NVL(:18,FAFP_S015_S012_TEC_ID)                 "
            + "            and FAFP_S015_TRO_ID      = NVL(:19,FAFP_S015_TRO_ID)                      "
            + "    )                                                                                  "
            + "    USI  ,                                                                             "
            + "    S015_TRO TRO,                                                                      "
            + "    PPM_LIBELLES_ZONE_VEHIC                                                            "
            + "where                                                                                  "
            + "    USI.INDI_ID              = FOP.INDI_ID                                             "
            + "    and ( (:20 is null and :21 is null)                                                "
            + "          or ( exists (                                                                "
            + "                select                                                                 "
            + "                    'x'                                                                "
            + "                from                                                                   "
            + "                     PPM_FOP_A_INDICEES,                                               "
            + "                     PPM_FAMILLES_FOP_A                                                "
            + "                where                                                                  "
            + "                     FAFP_S015_S012_TEC_ID     = NVL(:22,FAFP_S015_S012_TEC_ID)        "
            + "                     and FAFP_S015_TRO_ID      = NVL(:23,FAFP_S015_TRO_ID)             "
            + "                     and FAFP_INDI_ID           = INDI_ID                              "
            + "                     and INDI_ID               = FOP.INDI_ID                           "
            + "                     and FAFP_S004_FAM_ID      = :24                                   "
            + "                     and INDI_S015_S016_USI_ID = USI.S016_USI_ID)                      "
                              /* Optimisation Levon YAGDJIAN 26/10/2009 */
            + "               or  exists (                                                            "
            + "                     select  /*+ INDEX ( BRFS_OPERATIONS_UTILISEES OPUT_I1 ) */        "
            + "                           'x'                                                         "
            + "                     from                                                              "
            + "                          PPM_BLOCS,                                                   "
            + "                          S030_OPE,                                                    "
            + "                          BRFS_OPERATIONS_UTILISEES                                    "
            + "                     where                                                             "
            + "                          S030_PPM_BLOC_ID          = BLOC_ID                          "
            + "                          and OPUT_S030_ID          = S030_ID                          "
            + "                          and OPUT_S015_S012_TEC_ID = NVL(:25,OPUT_S015_S012_TEC_ID)   "
            + "                          and OPUT_S015_TRO_ID      = NVL(:26,OPUT_S015_TRO_ID)        "
            + "                          and OPUT_S004_FAM_ID      = :27                              "
            + "                          and BLOC_INDI_ID          = FOP.INDI_ID                      "
            + "                          and OPUT_S015_S016_USI_ID = :28)                             "
            + "    ))                                                                                 "
            + "    and ( (:29 is NULL and :30 is NULL)                                                "
            + "          or exists (                                                                  "
            + "              select                                                                   "
            + "                  'x'                                                                  "
            + "              from                                                                     "
            + "                  PPM_BLOCS,                                                           "
            + "                  S030_OPE,                                                            "
            + "                  BRFS_OPERATIONS_UTILISEES                                            "
            + "              where                                                                    "
            + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
            + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
            + "                  and OPUT_S030_ID          = S030_ID                                  "
            + "                    and OPUT_S004_FAM_ID      = :31                                    "
            + "                  and OPUT_S050_RMTC_ID     = NVL(:32,OPUT_S050_RMTC_ID)               "
            + "                  and NVL(S030_GFE_GFS,'*') = NVL(:33,NVL(S030_GFE_GFS,'*')))          "
            + "    )                                                                                  "
            + "    and ( (:34 is NULL and :35 is NULL and :36 is NULL )                               "
            + "          or (exists (                                                                 "
            + "              select                                                                   "
            + "                  'x'                                                                  "
            + "              from                                                                     "
            + "                  PPM_BLOCS,                                                           "
            + "                  S030_OPE,                                                            "
            + "               BRFS_OPERATIONS_UTILISEES                                               "
            + "              where                                                                    "
            + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
            + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
            + "                  and OPUT_S030_ID          = S030_ID                                  "
            + "                  and OPUT_FONCT            = NVL(:37,OPUT_FONCT)                      "
            + "                  and OPUT_S004_FAM_ID      = :38                                      "
            + "                  and :39 is null                                                      "
            + "                  and :40 is null)                                                     "
//            + "              or exists (                                                              "
//            + "                 select                                                                "
//            + "                        'x'                                                            "
//            + "                 from                                                                  "
//            + "                        PPM_BLOCS,                                                     "
//            + "                        S030_OPE,                                                      "
//            + "                        S040_OPE_COMP,                                                 "
//            + "                        S041_OPE_COMP_UTI,                                             "
//            + "                        BRFS_OPERATIONS_UTILISEES                                      "
//            + "                 where                                                                 "
//            + "                        BLOC_INDI_ID              = FOP.INDI_ID                        "
//            + "                        and S030_PPM_BLOC_ID      = BLOC_ID                            "
//            + "                        and S040_S030_ID          = S030_ID                            "
//            + "                        and NVL(S040_FF,'*')      = NVL(:41,NVL(S040_FF,'*'))          "
//            + "                        and NVL(S040_PG,'*')      = NVL(:42,NVL(S040_PG,'*'))          "
//            + "                        and S040_S025_REF_ID      = NVL(:43,S040_S025_REF_ID)          "
//            + "                        and S041_S040_ID          = S040_ID                            "
//            + "                        and S041_OPUT_ID          = OPUT_ID                            "
//            + "                        and OPUT_S030_ID          = S030_ID                            "
//            + "                        and OPUT_S015_S016_USI_ID = USI.S016_USI_ID                    "
//            + "                        and OPUT_S004_FAM_ID      = :44)                               "
			+ "              or exists (                                                              "
			+ "                 select                                                                "
			+ "                        'x'                                                            "
			+ "                 from                                                                  "
			+ "                        PPM_BLOCS,                                                     "
			+ "                        S030_OPE,                                                      "
			+ "                        S040_OPE_COMP,                                                 "
			+ "                        S041_OPE_COMP_UTI                                              "
			+ "                 where                                                                 "
			+ "                        BLOC_INDI_ID              = FOP.INDI_ID                        "
			+ "                        and S030_PPM_BLOC_ID      = BLOC_ID                            "
			+ "                        and S040_S030_ID          = S030_ID                            "
			+ "                        and NVL(S040_FF,'*')      = NVL(:41,NVL(S040_FF,'*'))          "
			+ "                        and NVL(S040_PG,'*')      = NVL(:42,NVL(S040_PG,'*'))          "
			+ "                        and S040_S025_REF_ID      = NVL(:43,S040_S025_REF_ID)          "
			+ "                        and S041_S040_ID          = S040_ID                            "
			+ "                        and S041_S016_USI_ID      = USI.S016_USI_ID                    "
			+ "                        and S040_FAM_FE           = :44)                               "
            + "               or exists (                                                             "
            + "                  select                                                               "
            + "                     'x'                                                               "
            + "                  from                                                                 "
            + "                     PPM_TAMPONS                                                       "
            + "                  where                                                                "
            + "                     TAMP_INDI_ID                  = FOP.INDI_ID                       "
            + "                     and NVL(TAMP_FONCTION,'*')    = NVL(:45,NVL(TAMP_FONCTION,'*'))   "
            + "                     and NVL(TAMP_PG,'*')          = NVL(:46,NVL(TAMP_PG,'*'))         "
            + "                     and NVL(TAMP_S025_REF_ID,'*') = NVL(:47,NVL(TAMP_S025_REF_ID,'*'))"
            + "                     and TAMP_TYPE                IN ('CONSO','SERRAGE')))             "
            + "    )                                                                                  "
            + "    and ( (:48 is NULL )                                                               "
            + "          or exists (                                                                  "
            + "              select                                                                   "
            + "                  'x'                                                                  "
            + "              from                                                                     "
            + "                  PPM_BLOCS,                                                           "
            + "                  S030_OPE,                                                            "
            + "                  BRFS_OPERATIONS_UTILISEES,                                           "
            + "                  S070_VEH,                                                            "
            + "                  S051_34CT,                                                           "
            + "                  S050_RMTC_S070_VEH                                                   "
            + "              where                                                                    "
            + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
            + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
            + "                  and OPUT_S030_ID          = S030_ID                                  "
            + "                  and OPUT_S004_FAM_ID      = S070_S051_S004_FAM_ID                    "
            + "                  and OPUT_S004_FAM_ID      = :49                                      "
            + "                  and S070_S016_USI_ID      = USI.S016_USI_ID                          "
            + "                  and S070_TYP              in ('J','S')                               "
            + "                  and S051_S004_FAM_ID      = S070_S051_S004_FAM_ID                    "
            + "                  and S051_S006_MIL_ID      = S070_S051_S006_MIL_ID                    "
            + "                  and S051_32CT_ID          = S070_S051_32CT_ID                        "
            + "                  and S051_32CT             = REPLACE(:50,'£','#')                     "
            + "                  and S050_S050_S004_FAM_ID = S070_S051_S004_FAM_ID                    "
            + "                  and S050_S050_RMTC_ID     = OPUT_S050_RMTC_ID                        "
            + "                  and S050_S050_S004_FAM_ID = OPUT_S004_FAM_ID                         "
            + "                  and S050_S070_ID          = S070_ID                                  "
            + "                  and RACC_COMPLETUDE       = 1 )                                      "
            + "    )                                                                                  "
            + "    and ( (:51 is NULL )                                                               "
            + "          or exists (                                                                  "
            + "              select                                                                   "
            + "                  'x'                                                                  "
            + "              from                                                                     "
            + "                   PPM_LIBELLES_FOP_A                                                  "
            + "              where                                                                    "
            + "                   LIND_INDI_ID         = FOP.INDI_ID                                  "
            + "                   and UPPER(LIND_NOM) like UPPER('%'||:52||'%')                       "
            + "                   and LIND_S005_LAN_ID = :53)                                         "
            + "    )                                                                                  "
            + "    and TRO.S015_S016_USI_ID   = USI.S016_USI_ID                                       "
            + "    and TRO.S015_S012_TEC_ID   = USI.S012_TEC_ID                                       "
            + "    and TRO.S015_TRO_ID        = USI.S015_TRO_ID                                       "
            + "    and LZON_ZON_METI_CODE (+) = 'MON'                                                 "
            + "    and LZON_ZONE_ID       (+) = FOPA_ZONE_ID                                          "
            + "    and LZON_S005_LAN_ID   (+) = :54                                                   "
            + "group by                                                                               "
            + "    DECODE(:1,'PRO',USI.S012_TEC_ID||USI.S015_TRO_ID,FOPA_ZONE_ID),                    "
            + "    DECODE(:2,'PRO',S015_LB,LZON_LIBELLE),                                             "
            + "    FOPA_ID,                                                                           "
            + "    FOPA_NUMERO,                                                                       "
            + "    REPLACE(REPLACE(LIND_NOM,CHR(13),' '),CHR(10),' ')                                 "
            + "order by                                                                               "
            + "    1,4)                                                                               ";



    /*
     * Modified By Vijayaraja (13/Mar/2009)
     */
    /** The Constant SEARCH_FOP_USI_NON_FOP. */
    private static final String SEARCH_FOP_USI_NON_FOP =
          "select FOPA_ZONE_ID, LZON_LIBELLE, FOPA_ID, FOPA_NUMERO, FOP_NAME, INDEX_IDS,          "
        + "     case                                                                              "
        + "        when instr(fam_list,',',1,6) > 0 then                                          "
        + "             substr(fam_list ,1,instr(fam_list,',',1,3))||'<BR>'                       "
        + "             ||substr(fam_list,instr(fam_list,',',1,3)+1,instr(fam_list,',',1,6)       "
        + "             -instr(fam_list,',',1,3))||'<BR>'||                                       "
        + "             substr(fam_list,instr(fam_list,',',1,6)+1)                                "
        + "             when instr(fam_list,',',1,3) > 0 then                                     "
        + "                  substr(fam_list ,1,instr(fam_list,',',1,3))||'<BR>'                  "
        + "                  ||substr(fam_list,instr(fam_list,',',1,3)+1)                         "
        + "             else fam_list end fam_list,                                               "
        + "             OPS_ERROR_FLAG                                                            "
        + " FROM (select                                                                          "
        + "    DECODE(:1,'PRO',USI.S012_TEC_ID||USI.S015_TRO_ID,FOPA_ZONE_ID)   FOPA_ZONE_ID,     "
        + "    DECODE(:2,'PRO',S015_LB,LZON_LIBELLE)                            LZON_LIBELLE,     "
        + "    FOPA_ID,                                                                           "
        + "    FOPA_NUMERO,                                                                       "
        + "    REPLACE(REPLACE(LIND_NOM,CHR(13),' '),CHR(10),' ')               FOP_NAME,         "
        + " STRAGG(distinct FOP.INDI_ID ||',')                                  INDEX_IDS,        "
        + " RTRIM(STRAGG(distinct FOP.FAFP_S004_FAM_ID   ||','),',')            FAM_LIST,         "
        + "    MAX(OPS_ERROR_FLAG) OPS_ERROR_FLAG                                             "
        + "from                                                                                   "
        + "       (select                                                                         "
        + "            FOPA_ID,                                                                   "
        + "            FAFP_S004_FAM_ID,                                                          "
        + "            FOPA_ZONE_ID,                                                              "
        + "            FOPA_ZONE_ID||FOPA_SEQUENCE||FOPA_PROXIMITE||FOPA_DIVERSITE FOPA_NUMERO,   "
        + "            IND.INDI_ID INDI_ID,                                                       "
        + "            LIND_NOM,                                                                  "
        + "            DERN.OPS_ERROR_FLAG                                                         "
        + "        from                                                                           "
        + "            PPM_FOP_A,                                                                 "
        + "            PPM_FOP_A_INDICEES IND,                                                    "
        + "            PPM_FAMILLES_FOP_A,                                                        "
        + "            (select                                                                    "
        + "                INDI_FOPA_ID,                                                          "
		+ "                max(INDI_ID) INDI_ID,                                                  "
		+ "                max(INDI_FLAG_OPSERROR) OPS_ERROR_FLAG                                 "
        + "             from                                                                      "
        + "                PPM_FOP_A_INDICEES,                                                    "
        + "                PPM_FOP_A_APPROPRIEES                                                  "
        + "             where                                                                     "
        + "                  APPR_INDI_ID              = INDI_ID                                  "
        + "                  and APPR_S015_S016_USI_ID = :3                                       "
        + "                  and INDI_ETAT            <> 'FIG'                                    "
        + "                  and APPR_S004_FAM_ID      = :4                                       "
        + "             group by                                                                  "
        + "                INDI_FOPA_ID) DERN,                                                    "
        + "            PPM_LIBELLES_FOP_A                                                         "
        + "        where                                                                          "
        + "                FOPA_ID = NVL(:5,FOPA_ID)                                              "
        + "            and FAFP_INDI_ID           = IND.INDI_ID                                   "
        + "            and FOPA_ZONE_ID           = NVL(:6,FOPA_ZONE_ID)                          "
        + "            and FOPA_METI_CODE         = 'MON'                                         "
        + "            and IND.INDI_ID            = NVL(:7,IND.INDI_ID)                           "
        + "            and IND.INDI_FOPA_ID       = FOPA_ID                                       "
        + "            and (IND.INDI_UTIL_IPN     = NVL(:8,IND.INDI_UTIL_IPN)                     "
        + "                 or IND.INDI_REDACTEUR = NVL(:9,IND.INDI_REDACTEUR))                   "
        + "            and ( (:10 = 'TOUS')                                                       "
        + "                   or (:11= 'CSR/PANIMMO'                                              "
        + "                       and (IND.INDI_FLAG_CSR='O' or IND.INDI_FLAG_PANIMO ='O'))       "
        + "                   or (:12 = 'CSR'     and IND.INDI_FLAG_CSR='O')                      "
        + "                   or (:13 = 'PANIMMO' and IND.INDI_FLAG_PANIMO ='O'))                 "
        + "            and IND.INDI_ETAT        = NVL(:14, IND.INDI_ETAT)                         "
        + "            and DERN.INDI_FOPA_ID    = IND.INDI_FOPA_ID                                "
        + "            and LIND_INDI_ID     (+) = DERN.INDI_ID                                    "
        + "            and LIND_S005_LAN_ID (+) = :15)                                            "
        + "    FOP,                                                                               "
        + "       (select                                                                         "
        + "           APPR_INDI_ID          INDI_ID,                                              "
        + "           APPR_S015_S016_USI_ID S016_USI_ID,                                          "
        + "           APPR_S015_S012_TEC_ID S012_TEC_ID,                                          "
        + "           APPR_S015_TRO_ID      S015_TRO_ID                                           "
        + "        from                                                                           "
        + "           PPM_FOP_A_APPROPRIEES                                                       "
        + "        where                                                                          "
        + "           APPR_S015_S016_USI_ID     = :16                                             "
        + "           and APPR_S004_FAM_ID      = :17                                             "
        + "           and APPR_S015_S012_TEC_ID = NVL(:18,APPR_S015_S012_TEC_ID)                  "
        + "           and APPR_S015_TRO_ID      = NVL(:19,APPR_S015_TRO_ID))                      "
        + "    USI  ,                                                                             "
        + "    S015_TRO TRO,                                                                      "
        + "    PPM_LIBELLES_ZONE_VEHIC                                                            "
        + "where                                                                                  "
        + "    USI.INDI_ID              = FOP.INDI_ID                                             "
        + "    and ( (:20 is null and :21 is null)                                                "
        + "          or ( exists (                                                                "
        + "                select                                                                 "
        + "                    'x'                                                                "
        + "                from                                                                   "
        + "                     PPM_FOP_A_APPROPRIEES                                             "
        + "                where                                                                  "
        + "                     APPR_S015_S012_TEC_ID     = NVL(:22,APPR_S015_S012_TEC_ID)        "
        + "                     and APPR_S015_TRO_ID      = NVL(:23,APPR_S015_TRO_ID)             "
        + "                     and APPR_S004_FAM_ID      = :24                                   "
        + "                     and APPR_INDI_ID          = FOP.INDI_ID                           "
        + "                     and APPR_S015_S016_USI_ID = USI.S016_USI_ID)                      "
                              /* Optimisation Levon YAGDJIAN 26/10/2009 */
        + "               or  exists (                                                            "
        + "                     select  /*+ INDEX ( BRFS_OPERATIONS_UTILISEES OPUT_I1 ) */        "
        + "                           'x'                                                         "
        + "                     from                                                              "
        + "                          PPM_BLOCS,                                                   "
        + "                          S030_OPE,                                                    "
        + "                          BRFS_OPERATIONS_UTILISEES                                    "
        + "                     where                                                             "
        + "                          S030_PPM_BLOC_ID          = BLOC_ID                          "
        + "                          and OPUT_S030_ID          = S030_ID                          "
        + "                          and OPUT_S015_S012_TEC_ID = NVL(:25,OPUT_S015_S012_TEC_ID)   "
        + "                          and OPUT_S015_TRO_ID      = NVL(:26,OPUT_S015_TRO_ID)        "
        + "                          and OPUT_S004_FAM_ID      = :27                              "
        + "                          and BLOC_INDI_ID          = FOP.INDI_ID                      "
        + "                          and OPUT_S015_S016_USI_ID = :28)                             "
        + "    ))                                                                                 "
        + "    and ( (:29 is NULL and :30 is NULL)                                                "
        + "          or exists (                                                                  "
        + "              select                                                                   "
        + "                  'x'                                                                  "
        + "              from                                                                     "
        + "                  PPM_BLOCS,                                                           "
        + "                  S030_OPE,                                                            "
        + "                  BRFS_OPERATIONS_UTILISEES                                            "
        + "              where                                                                    "
        + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
        + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
        + "                  and OPUT_S030_ID        = S030_ID                                    "
        + "                  and OPUT_S004_FAM_ID      = :31                                      "
        + "                  and OPUT_S050_RMTC_ID       = NVL(:32,OPUT_S050_RMTC_ID)             "
        + "                  and NVL(S030_GFE_GFS,'*') = NVL(:33,NVL(S030_GFE_GFS,'*')))          "
        + "    )                                                                                  "
        + "    and ( (:34 is NULL and :35 is NULL and :36 is NULL )                               "
        + "          or (exists (                                                                 "
        + "              select                                                                   "
        + "                  'x'                                                                  "
        + "              from                                                                     "
        + "                  PPM_BLOCS,                                                           "
        + "                  S030_OPE,                                                            "
        + "             BRFS_OPERATIONS_UTILISEES                                                 "
        + "              where                                                                    "
        + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
        + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
        + "                  and OPUT_S030_ID          = S030_ID                                  "
        + "                  and OPUT_FONCT            = NVL(:37,OPUT_FONCT)                      "
        + "                  and OPUT_S004_FAM_ID      = :38                                      "
        + "                  and :39 is null                                                      "
        + "                  and :40 is null)                                                     "
//        + "              or exists (                                                              "
//        + "                 select                                                                "
//        + "                        'x'                                                            "
//        + "                 from                                                                  "
//        + "                        PPM_BLOCS,                                                     "
//        + "                        S030_OPE,                                                      "
//        + "                        S040_OPE_COMP,                                                 "
//        + "                        S041_OPE_COMP_UTI,                                             "
//        + "                        BRFS_OPERATIONS_UTILISEES                                      "
//        + "                 where                                                                 "
//        + "                        BLOC_INDI_ID              = FOP.INDI_ID                        "
//        + "                        and S030_PPM_BLOC_ID      = BLOC_ID                            "
//        + "                        and S040_S030_ID          = S030_ID                            "
//        + "                        and NVL(S040_FF,'*')      = NVL(:41,NVL(S040_FF,'*'))          "
//        + "                        and NVL(S040_PG,'*')      = NVL(:42,NVL(S040_PG,'*'))          "
//        + "                        and S040_S025_REF_ID      = NVL(:43,S040_S025_REF_ID)          "
//        + "                        and S041_S040_ID          = S040_ID                            "
//        + "                        and S041_OPUT_ID          = OPUT_ID                            "
//        + "                        and OPUT_S030_ID          = S030_ID                            "
//        + "                        and OPUT_S015_S016_USI_ID = USI.S016_USI_ID                    "
//        + "                        and OPUT_S004_FAM_ID     = :44)                                "
		+ "              or exists (                                                              "
		+ "                 select                                                                "
		+ "                        'x'                                                            "
		+ "                 from                                                                  "
		+ "                        PPM_BLOCS,                                                     "
		+ "                        S030_OPE,                                                      "
		+ "                        S040_OPE_COMP,                                                 "
		+ "                        S041_OPE_COMP_UTI                                              "
		+ "                 where                                                                 "
		+ "                        BLOC_INDI_ID              = FOP.INDI_ID                        "
		+ "                        and S030_PPM_BLOC_ID      = BLOC_ID                            "
		+ "                        and S040_S030_ID          = S030_ID                            "
		+ "                        and NVL(S040_FF,'*')      = NVL(:41,NVL(S040_FF,'*'))          "
		+ "                        and NVL(S040_PG,'*')      = NVL(:42,NVL(S040_PG,'*'))          "
		+ "                        and S040_S025_REF_ID      = NVL(:43,S040_S025_REF_ID)          "
		+ "                        and S041_S040_ID          = S040_ID                            "
		+ "                        and S041_S016_USI_ID      = USI.S016_USI_ID                    "
		+ "                        and S040_FAM_FE           = :44)                               "        
        + "               or exists (                                                             "
        + "                  select                                                               "
        + "                     'x'                                                               "
        + "                  from                                                                 "
        + "                     PPM_TAMPONS                                                       "
        + "                  where                                                                "
        + "                     TAMP_INDI_ID                  = FOP.INDI_ID                       "
        + "                     and NVL(TAMP_FONCTION,'*')    = NVL(:45,NVL(TAMP_FONCTION,'*'))   "
        + "                     and NVL(TAMP_PG,'*')          = NVL(:46,NVL(TAMP_PG,'*'))         "
        + "                     and NVL(TAMP_S025_REF_ID,'*') = NVL(:47,NVL(TAMP_S025_REF_ID,'*'))"
        + "                     and TAMP_TYPE                IN ('CONSO','SERRAGE')))             "
        + "    )                                                                                  "
        + "    and ( (:48 is NULL )                                                               "
        + "          or exists (                                                                  "
        + "              select                                                                   "
        + "                  'x'                                                                  "
        + "              from                                                                     "
        + "                  PPM_BLOCS,                                                           "
        + "                  S030_OPE,                                                            "
        + "                  S070_VEH,                                                            "
        + "                  BRFS_OPERATIONS_UTILISEES,                                           "
        + "                  S051_34CT,                                                           "
        + "                  S050_RMTC_S070_VEH                                                   "
        + "              where                                                                    "
        + "                  BLOC_INDI_ID              = FOP.INDI_ID                              "
        + "                  and S030_PPM_BLOC_ID      = BLOC_ID                                  "
        + "                  and OPUT_S030_ID          = S030_ID                                  "
        + "                  and OPUT_S004_FAM_ID      = S070_S051_S004_FAM_ID                    "
        + "                  and OPUT_S004_FAM_ID      = :49                                      "
        + "                  and S070_S016_USI_ID      = USI.S016_USI_ID                          "
        + "                  and S070_TYP              in ('J','S')                               "
        + "                  and S051_S004_FAM_ID      = S070_S051_S004_FAM_ID                    "
        + "                  and S051_S006_MIL_ID      = S070_S051_S006_MIL_ID                    "
        + "                  and S051_32CT_ID          = S070_S051_32CT_ID                        "
        + "                  and S051_32CT             = REPLACE(:50,'£','#')                     "
        + "                  and S050_S050_S004_FAM_ID = S070_S051_S004_FAM_ID                    "
        + "                  and S050_S050_RMTC_ID     = OPUT_S050_RMTC_ID                        "
        + "                  and S050_S050_S004_FAM_ID = OPUT_S004_FAM_ID                         "
        + "                  and S050_S070_ID          = S070_ID                                  "
        + "                  and RACC_COMPLETUDE       = 1 )                                      "
        + "    )                                                                                  "
        + "    and ( (:51 is NULL )                                                               "
        + "          or exists (                                                                  "
        + "              select                                                                   "
        + "                  'x'                                                                  "
        + "              from                                                                     "
        + "                   PPM_LIBELLES_FOP_A                                                  "
        + "              where                                                                    "
        + "                   LIND_INDI_ID         = FOP.INDI_ID                                  "
        + "                   and UPPER(LIND_NOM) like UPPER('%'||:52||'%')                       "
        + "                   and LIND_S005_LAN_ID = :53)                                         "
        + "    )                                                                                  "
        + "    and TRO.S015_S016_USI_ID   = USI.S016_USI_ID                                       "
        + "    and TRO.S015_S012_TEC_ID   = USI.S012_TEC_ID                                       "
        + "    and TRO.S015_TRO_ID        = USI.S015_TRO_ID                                       "
        + "    and LZON_ZON_METI_CODE (+) = 'MON'                                                 "
        + "    and LZON_ZONE_ID       (+) = FOPA_ZONE_ID                                          "
        + "    and LZON_S005_LAN_ID   (+) = :54                                                   "
        + "group by                                                                               "
        + "    DECODE(:1,'PRO',USI.S012_TEC_ID||USI.S015_TRO_ID,FOPA_ZONE_ID),                    "
        + "    DECODE(:2,'PRO',S015_LB,LZON_LIBELLE),                                             "
        + "    FOPA_ID,                                                                           "
        + "    FOPA_NUMERO,                                                                       "
        + "    REPLACE(REPLACE(LIND_NOM,CHR(13),' '),CHR(10),' ')                                 "
        + "order by                                                                               "
        + "    1,4)                                                                               ";

    /*
     * Recherche des attributs d'une FOP indicée
     *
     *  Emmanuel Bartoli (ATOS ORIGIN) le 20/05/2008
     *  Lot 820 - Recherche du dernier indice officiel
     *  Modified By Vijayaraja (13/Mar/2009)
     *
     *  Modified By Emmanuel Bartoli (27/10/2009)
     *   ALL_FAMILIES : a "select" must never follow another "select"
     *   => I use the same writing as in  SEARCH_FOP_USI_FOP   "select...from ... elect" instead of "select...select"
     *
    /** The Constant GET_FOP_A_IND_USI_FOP. */
    private static final String GET_FOP_A_IND_USI_FOP =
          "select                                                                   "
        + "    INDI_ID,                                                             "
        + "    FOPA_ID,                                                             "
        + "    INDI_ETAT,                                                           "
        + "    INDI_INDICE,                                                         "
        + "    INDI_UTIL_IPN,                                                       "
        + "    LIND_NOM,                                                            "
        + "    S012_TEC_ID,                                                         "
        + "    S015_TRO_ID,                                                         "
        + "    DATE_DEB,                                                            "
        + "    DATE_FIN,                                                            "
        + "    SOURCE,                                                              "
        + "    DERN_IND_OFF,                                                        "
        + "        GET_TEC_TRO_FOP(INDI_ID,S016_USI_ID,S004_FAM_ID)                 "
        + "    TEC_TRO,                                                             "
        + "    case                                                                 "
        + "        when instr(FAM_LIST,',',1,6) > 0 then                            "
        + "         substr(FAM_LIST ,1,instr(FAM_LIST,',',1,3))||'<BR>'             "
        + "         ||substr(FAM_LIST,instr(FAM_LIST,',',1,3)+1,                    "
        + "         instr(FAM_LIST,',',1,6)-instr(FAM_LIST,',',1,3))||'<BR>'||      "
        + "         substr(FAM_LIST,instr(FAM_LIST,',',1,6)+1)                      "
        + "        when instr(FAM_LIST,',',1,3) > 0 then                            "
        + "         substr(FAM_LIST ,1,instr(FAM_LIST,',',1,3))||'<BR>'             "
        + "         ||substr(FAM_LIST,instr(FAM_LIST,',',1,3)+1)                    "
        + "        else FAM_LIST                                                    "
        + "    end ALL_FAMILIES                                                     "
        + "from                                                                     "
        + "    (select                                                              "
        + "        IND.INDI_ID             INDI_ID,                                 "
        + "        FOPA_ID                 FOPA_ID,                                 "
        + "        IND.INDI_ETAT           INDI_ETAT,                               "
        + "        IND.INDI_INDICE         INDI_INDICE,                             "
        + "        IND.INDI_UTIL_IPN       INDI_UTIL_IPN,                           "
        + "        LIND_NOM                LIND_NOM,                                "
        + "        FAFP_S015_S012_TEC_ID   S012_TEC_ID,                             "
        + "        FAFP_S015_TRO_ID        S015_TRO_ID,                             "
        + "        NULL                    DATE_DEB,                                "
        + "        NULL                    DATE_FIN,                                "
        + "        DECODE(INDI_SOURCE_FAM_ID,NULL,'N','O')                          "
        + "                                SOURCE,                                  "
        + "        DECODE(IND.INDI_ID,DERN_IND_OFF.INDI_ID,'O','N')                 "
        + "                                DERN_IND_OFF,                            "
        + "        INDI_S015_S016_USI_ID   S016_USI_ID,                             "
        + "        FAM.FAFP_S004_FAM_ID    S004_FAM_ID,                             "
        + "        RTRIM(STRAGG(distinct FAM_LIST.FAFP_S004_FAM_ID  ||',') ,',')    "
        + "                                FAM_LIST                                 "
        + "    from                                                                 "
        + "        PPM_FOP_A,                                                       "
        + "        PPM_FOP_A_INDICEES IND,                                          "
        + "        PPM_LIBELLES_FOP_A,                                              "
        + "        PPM_FAMILLES_FOP_A FAM,                                          "
        + "           (select                                                       "
        + "               INDI_FOPA_ID,                                             "
        + "               max(INDI_ID) INDI_ID                                      "
        + "            from                                                         "
        + "               PPM_FOP_A_INDICEES                                        "
        + "            where                                                        "
        + "               INDI_ETAT         = 'OFF'                                 "
        + "            group by                                                     "
        + "               INDI_FOPA_ID)                                             "
        + "        DERN_IND_OFF,                                                    "
        + "            (select                                                      "
        + "                FAFP_S004_FAM_ID                                         "
        + "             from                                                        "
        + "                PPM_FAMILLES_FOP_A                                       "
        + "             where                                                       "
        + "                FAFP_INDI_ID =  :1)                                      "
        + "        FAM_LIST                                                         "
        + "    where                                                                "
        + "        IND.INDI_ID                        = :2                          "
        + "        and IND.INDI_FOPA_ID               = FOPA_ID                     "
        + "        and FAM.FAFP_INDI_ID               = IND.INDI_ID                 "
        + "        and FAM.FAFP_S004_FAM_ID           = :3                          "
        + "        and LIND_INDI_ID     (+)           = IND.INDI_ID                 "
        + "        and LIND_S005_LAN_ID (+)           = :4                          "
        + "        and INDI_S015_S016_USI_ID          = :5                          "
        + "        and DERN_IND_OFF.INDI_FOPA_ID (+)  = FOPA_ID                     "
        + "     group by                                                            "
        + "        IND.INDI_ID,                                                     "
        + "        FOPA_ID,                                                         "
        + "        IND.INDI_ETAT,                                                   "
        + "        IND.INDI_INDICE,                                                 "
        + "        IND.INDI_UTIL_IPN,                                               "
        + "        LIND_NOM,                                                        "
        + "        FAFP_S015_S012_TEC_ID,                                           "
        + "        FAFP_S015_TRO_ID,                                                "
        + "        DECODE(INDI_SOURCE_FAM_ID,NULL,'N','O') ,                        "
        + "        DECODE(IND.INDI_ID,DERN_IND_OFF.INDI_ID,'O','N'),                "
        + "        INDI_S015_S016_USI_ID,                                           "
        + "        FAM.FAFP_S004_FAM_ID)                                            ";

    /*
     * Modified By Vijayaraja (13/Mar/2009)
     *
     *  Modified By Emmanuel Bartoli (27/10/2009)
     *  => "select...from...select" instead of "select...select"
     */
    /** The Constant GET_FOP_A_IND_USI_NON_FOP. */
    private static final String GET_FOP_A_IND_USI_NON_FOP =
          "select                                                                "
        + "    INDI_ID,                                                          "
        + "    FOPA_ID,                                                          "
        + "    INDI_ETAT,                                                        "
        + "    INDI_INDICE,                                                      "
        + "    INDI_UTIL_IPN,                                                    "
        + "    LIND_NOM,                                                         "
        + "    S012_TEC_ID,                                                      "
        + "    S015_TRO_ID,                                                      "
        + "    DATE_DEB,                                                         "
        + "    DATE_FIN,                                                         "
        + "    SOURCE,                                                           "
        + "    DERN_IND_OFF,                                                     "
        + "        GET_TEC_TRO_FOP(INDI_ID,S016_USI_ID,S004_FAM_ID)              "
        + "    TEC_TRO,                                                          "
        + "    case                                                              "
        + "        when instr(FAM_LIST,',',1,6) > 0 then                         "
        + "         substr(FAM_LIST ,1,instr(FAM_LIST,',',1,3))||'<BR>'          "
        + "         ||substr(FAM_LIST,instr(FAM_LIST,',',1,3)+1,                 "
        + "         instr(FAM_LIST,',',1,6)-instr(FAM_LIST,',',1,3))||'<BR>'||   "
        + "         substr(FAM_LIST,instr(FAM_LIST,',',1,6)+1)                   "
        + "        when instr(FAM_LIST,',',1,3) > 0 then                         "
        + "         substr(FAM_LIST ,1,instr(FAM_LIST,',',1,3))||'<BR>'          "
        + "         ||substr(FAM_LIST,instr(FAM_LIST,',',1,3)+1)                 "
        + "        else FAM_LIST                                                 "
        + "    end ALL_FAMILIES                                                  "
        + "from                                                                  "
        + "    (select                                                           "
        + "        IND.INDI_ID                                 INDI_ID,          "
        + "        FOPA_ID                                     FOPA_ID,          "
        + "        IND.INDI_ETAT                               INDI_ETAT,        "
        + "        IND.INDI_INDICE                             INDI_INDICE,      "
        + "        IND.INDI_UTIL_IPN                           INDI_UTIL_IPN,    "
        + "        LIND_NOM                                    LIND_NOM,         "
        + "        APPR_S015_S012_TEC_ID                       S012_TEC_ID,      "
        + "        APPR_S015_TRO_ID                            S015_TRO_ID,      "
        + "        TO_CHAR(APPR_DATE_DEB,'IW/IYYY')            DATE_DEB,         "
        + "        NVL(TO_CHAR(APPR_DATE_FIN,'IW/IYYY'),'NS')  DATE_FIN,         "
        + "        DECODE(INDI_SOURCE_FAM_ID,NULL,'N','O')     SOURCE,           "
        + "        DECODE(IND.INDI_ID,DERN_IND_OFF.INDI_ID,'O','N')              "
        + "                                                    DERN_IND_OFF,     "
        + "        APPR_S015_S016_USI_ID                       S016_USI_ID,      "
        + "        APPR_S004_FAM_ID                            S004_FAM_ID,      "
        + "        RTRIM(STRAGG(distinct FAM_LIST.FAFP_S004_FAM_ID  ||',') ,',') "
        + "                                                    FAM_LIST          "
        + "    from                                                              "
        + "        PPM_FOP_A,                                                    "
        + "        PPM_FOP_A_INDICEES IND,                                       "
        + "        PPM_LIBELLES_FOP_A,                                           "
        + "           (select                                                    "
        + "               INDI_FOPA_ID,                                          "
        + "               max(INDI_ID) INDI_ID                                   "
        + "            from                                                      "
        + "               PPM_FOP_A_INDICEES                                     "
        + "            where                                                     "
        + "               INDI_ETAT         = 'OFF'                              "
        + "            group by                                                  "
        + "               INDI_FOPA_ID)                                          "
        + "        DERN_IND_OFF,                                                 "
        + "        PPM_FOP_A_APPROPRIEES,                                        "
        + "            (select                                                   "
        + "                FAFP_S004_FAM_ID                                      "
        + "             from                                                     "
        + "                PPM_FAMILLES_FOP_A                                    "
        + "             where                                                    "
        + "                FAFP_INDI_ID =  :1)                                   "
        + "        FAM_LIST                                                      "
        + "    where                                                             "
        + "        IND.INDI_ID                        = :2                       "
        + "        and APPR_INDI_ID                   = IND.INDI_ID              "
        + "        and IND.INDI_FOPA_ID               = FOPA_ID                  "
        + "        and APPR_S004_FAM_ID               = :3                       "
        + "        and LIND_INDI_ID     (+)           = IND.INDI_ID              "
        + "        and LIND_S005_LAN_ID (+)           = :4                       "
        + "        and APPR_S015_S016_USI_ID          = :5                       "
        + "        and DERN_IND_OFF.INDI_FOPA_ID (+)  = FOPA_ID                  "
        + "     group by                                                         "
        + "        IND.INDI_ID,                                                  "
        + "        FOPA_ID,                                                      "
        + "        IND.INDI_ETAT,                                                "
        + "        IND.INDI_INDICE,                                              "
        + "        IND.INDI_UTIL_IPN,                                            "
        + "        LIND_NOM,                                                     "
        + "        APPR_S015_S012_TEC_ID,                                        "
        + "        APPR_S015_TRO_ID,                                             "
        + "        TO_CHAR(APPR_DATE_DEB,'IW/IYYY'),                             "
        + "        NVL(TO_CHAR(APPR_DATE_FIN,'IW/IYYY'),'NS'),                   "
        + "        DECODE(INDI_SOURCE_FAM_ID,NULL,'N','O') ,                     "
        + "        DECODE(IND.INDI_ID,DERN_IND_OFF.INDI_ID,'O','N'),             "
        + "        APPR_S015_S016_USI_ID,                                        "
        + "        APPR_S004_FAM_ID)                                             ";

    /*
     * Recherche de l'id d'une FOP A
     * Modified By Vijayaraja (13/Mar/2009)
     * Modified By Emmanuel Bartoli (12/Oct/2009)
     */
    /** The Constant GET_ID_FOP_A. */
    private static final String GET_ID_FOP_A =
              "select                            "
            + "   FOPA_ID                        "
            + "from                              "
            + "   PPM_FOP_A,                     "
            + "   PPM_FOP_A_INDICEES,            "
            + "   PPM_FAMILLES_FOP_A             "
            + "where                             "
            + "      FOPA_METI_CODE   = 'MON'    "
            + "  and INDI_FOPA_ID     = FOPA_ID  "
            + "  and FAFP_INDI_ID     = INDI_ID  "
            + "  and FAFP_S004_FAM_ID = :1       "
            + "  and FOPA_ZONE_ID     = :2       "
            + "  and FOPA_SEQUENCE  ||           "
            + "      FOPA_PROXIMITE ||           "
            + "      FOPA_DIVERSITE    =:3       ";

    /*
     * Recherche de l'id d'une FOP A Indicée ou Archivée
     */
    /** The Constant GET_ID_FOP_A_IND_ARCH. */
    private static final String GET_ID_FOP_A_IND_ARCH =
              "select                                                     "
            + "   INDI_ID                                                 "
            + "from                                                       "
            + "   PPM_FOP_A_INDICEES                                      "
            + "where                                                      "
            + "   INDI_FOPA_ID = :1                                       "
            + "   and INDI_INDICE = :2                                    "
            + "union                                                      "
            + "select                                                     "
            + "   -1                                                      "
            + "from                                                       "
            + "   PPM_FOP_A_ARCHIVEES                                     "
            + "where                                                      "
            + "  ARCH_S004_FAM_ID          = :3                           "
            + "  and ARCH_S015_S016_USI_ID = 'FOP'                        "
            + "  and ARCH_METI_CODE        = 'MON'                        "
            + "  and ARCH_S015_S012_TEC_ID = NVL(:4,ARCH_S015_S012_TEC_ID)"
            + "  and ARCH_S015_TRO_ID      = NVL(:5,ARCH_S015_TRO_ID)     "
            + "  and ARCH_ZONE_ID   ||                                    "
            + "      ARCH_SEQUENCE  ||                                    "
            + "      ARCH_PROXIMITE ||                                    "
            + "      ARCH_DIVERSITE        = :6                           "
            + "  and ARCH_INDICE           = :7                           ";

    /*
     * Recherche de la liste de FOP A Archivées
     */
    /** The Constant SEARCH_FOP_ARCH. */
    private static final String SEARCH_FOP_ARCH =
              "select                                                         "
            + "   ARCH_INDICE,                                                "
            + "   LARC_NOM,                                                   "
            + "   ARCH_S015_S012_TEC_ID,                                      "
            + "   ARCH_S015_TRO_ID,                                           "
            + "   S005_LAN_AQR                                                "
            + "from                                                           "
            + "   PPM_FOP_A_ARCHIVEES,                                        "
            + "   PPM_LIBELLES_FOP_A_ARCHIVEE,                                "
            + "   S005_LAN                                                    "
            + "where                                                          "
            + "       ARCH_S004_FAM_ID      = :1                              "
            + "   and ARCH_S015_S016_USI_ID = 'FOP'                           "
            + "   and ARCH_METI_CODE        = 'MON'                           "
            + "   and ARCH_S015_S012_TEC_ID = NVL(:2,ARCH_S015_S012_TEC_ID)   "
            + "   and ARCH_S015_TRO_ID      = NVL(:3,ARCH_S015_TRO_ID)        "
            + "   and ARCH_ZONE_ID   ||                                       "
            + "       ARCH_SEQUENCE  ||                                       "
            + "       ARCH_PROXIMITE ||                                       "
            + "       ARCH_DIVERSITE        = :4                              "
            + "   and ARCH_INDICE           = NVL(:5,ARCH_INDICE)             "
            + "   and LARC_ARCH_ID          = ARCH_ID                         "
            + "   and LARC_S005_LAN_ID      = S005_LAN_ID                     "
            + "order by                                                       "
            + "   TO_NUMBER(DECODE(ARCH_INDICE,'X',0,'N',0,ARCH_INDICE)) DESC,"
            + "   S005_LAN_AQR                                                ";


    /**
     * Init the logger.
     */
    public static void init() {
        //Init the logger
        oLogService =
            new LogService("com.hubino.pros.backend.fop.recherche.FopMontageSearchDAO");
    }

    /**
     * Recherche de l'id d'une FOP A.
     *
     * @param connection
     *            the connection
     * @param sFamille
     *            the s famille
     * @param sZoneVeh
     *            the s zone veh
     * @param sNumero4c
     *            the s numero4c
     *
     * @return the id fop a
     *
     * @throws SQLException
     *             the SQL exception
     */
    public static String getIdFopA(
        Connection connection,
        String sFamille,
        String sZoneVeh,
        String sNumero4c)
        throws SQLException {

        String sId = "";

        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            pstmt = connection.prepareStatement(GET_ID_FOP_A);
            pstmt.setString(1 ,sFamille);
            pstmt.setString(2 ,sZoneVeh);
            pstmt.setString(3 ,sNumero4c);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                sId = rs.getString(1);
            }

            return sId;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
        }

    }

    /**
     * Recherche de l'id d'une FOP A Indicée ou Archivée.
     *
     * @param connection
     *            the connection
     * @param sFamille
     *            the s famille
     * @param sNumero
     *            the s numero
     * @param sFopAId
     *            the s fop a id
     * @param sIndice
     *            the s indice
     * @param sTechnique
     *            the s technique
     * @param sTroncon
     *            the s troncon
     *
     * @return the id fop a ind arch
     *
     * @throws SQLException
     *             the SQL exception
     */
    public static String getIdFopAIndArch(
        Connection connection,
        String sFopAId,
        String sIndice,
        String sFamille,
        String sTechnique,
        String sTroncon,
        String sNumero)
        throws SQLException {

        String sId = "";

        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            pstmt = connection.prepareStatement(GET_ID_FOP_A_IND_ARCH);
            pstmt.setString(1 ,sFopAId);
            pstmt.setString(2 ,sIndice);
            pstmt.setString(3 ,sFamille);
            pstmt.setString(4 ,sTechnique);
            pstmt.setString(5 ,sTroncon);
            pstmt.setString(6 ,sNumero);
            pstmt.setString(7 ,sIndice);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                sId = rs.getString(1);
            }

            return sId;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
        }

    }


    /**
     * Recherche de la liste des Fop A
     *
     * Cette méthode est utilisée pour chercher : - une liste de vues
     * (sIdVueDeploy vaut chaîne vide "" ) si la liste de vue ne contient qu'une
     * seule vue,on récupère aussi les FOP de cette VUE - une liste des FOP A
     * d'une vue PROcess(sIdVueDeploy est différent de chaîne vide "") - une
     * liste des FOP A d'une vue ZONe vehicule (sIdVueDeploy est différent de
     * chaîne vide "").
     *
     * @param connection
     *            the connection
     * @param oFopSearch
     *            the o fop search
     * @param sUserIpn
     *            the s user ipn
     * @param sIdVueDeploy
     *            the s id vue deploy
     *
     * @return the list
     *
     * @throws SQLException
     *             the SQL exception
     */
    public static List searchFopList(
                Connection connection,
                FopSearch oFopSearch,
                String sUserIpn,
                String sIdVueDeploy)
        throws SQLException {

        // Init the logger
        init();
        oLogService.debug("DEBUT  FopMontageSearchDAO.getFopAList()");
        //oLogService.debug(oFopSearch.toString());

        // Liste des FOP
        List oList = new ArrayList();

        // Indique si l'indice recherché est archivé ou non
        boolean isIndiceArchive = false;

        // Recherche de l'usine de la recherche
        Usine oUsine =
            UsineDAO.fineCodeUsineFopByLibelle(
                connection,
                oFopSearch.getUsine());

        // Cas de tous les états de FOP (Brouillon et Officiel)
        if (oFopSearch.getEtat().equals(Constants.CST_ALL)){
            oFopSearch.setEtat("");
        }

        // On cherche la liste des FOP A d'une vue en particulier,
        // avec une technique/tronçon ou une zone véhicule particulière
        String sTechniqueSearch = "";
        String sTronconSearch   = "";
        String sZoneVehSearch   = oFopSearch.getZoneVeh();

        if (!sIdVueDeploy.equals("")) {
            VueFopMontage oVueFopMontage =
                new VueFopMontage(sIdVueDeploy, oFopSearch.getVue());
            // La vue est PROcess
            if (oVueFopMontage.getType().equals(Constants.CST_PRO)) {
                sTechniqueSearch = oVueFopMontage.getTechnique();
                sTronconSearch   = oVueFopMontage.getTroncon();
            }
            // La vue est ZONe véhicule
            else if (oVueFopMontage.getType().equals(Constants.CST_ZON)) {
                sZoneVehSearch = oVueFopMontage.getZoneVeh();
            }

        }

        // Cas du numéro de FOP saisi
        // On recherche l'id de la FOP
        if ((!oFopSearch.getNumero().equals(""))
            && (!oFopSearch.getZoneVeh().equals(""))) {
            oFopSearch.setIdFopA(
                getIdFopA(
                    connection,
                    oFopSearch.getFamille(),
                    sZoneVehSearch,
                    oFopSearch.getNumero()));
            // La FOP n'a pas été trouvée.
            if (oFopSearch.getIdFopA().equals("")) {
                return  oList;
            }

            // Cas de l'indice saisi
            if (!oFopSearch.getIndice().equals("")) {
                // On recherche l'id de la FOP Indicée
                String sIdIdFopAIndArch =
                    getIdFopAIndArch(
                        connection,
                        oFopSearch.getIdFopA(),
                        oFopSearch.getIndice(),
                        oFopSearch.getFamille(),
                        oFopSearch.getTechnique(),
                        oFopSearch.getTroncon(),
                        oFopSearch.getZoneVeh() + oFopSearch.getNumero());

                // L'indice n'est pas été trouvé
                if (sIdIdFopAIndArch.equals("")) {
                    return  oList;
                }
                // L'indice n'est pas archivée
                else if (!sIdIdFopAIndArch.equals("-1")) {
                    oFopSearch.setIdFopAIndice(sIdIdFopAIndArch);
                }
                // L'indice est archivée,on recherche donc tous les indices
                // pour avoir au moins une vue/FOP A dans laquelle afficher cet indice archivé
                // Néanmoins,L'archivage de concerne que l'usine FOP et
                // l'état de recherche ne doit pas être BROuillon ou OFFiciel
                else if (
                    (oFopSearch
                        .getUsine()
                        .toUpperCase()
                        .equals(Constants.CST_DEFAULT_FACTORY))
                        && ((oFopSearch.getEtat().equals(""))
                            || (oFopSearch
                                .getEtat()
                                .equals(Constants.CST_ALL)))) {
                    isIndiceArchive = true;
                }
                // L'indice est archivé mais on ne peut l'afficher
                else {
                    return  oList;
                }

            }
        }

        // Test si l'utilisateur est administrateur
        boolean bIsAdmin = UserDAO.isAdmin(connection, sUserIpn);

        // Restriction de l'utilisateur à des techniques/tronçons pour la famille/usine (cas minoritaires)
        // Si restriction il y a,il faudra éliminer les FOP non autorisées
        // Note : le contrôle d'accès sur la famille/usine/technique/tronçon a été réalisé précédemment.
        boolean bTecTroProRestriction = false;
        if (oFopSearch.getVue().equals(Constants.CST_PRO)) {
            if (!bIsAdmin) {
                bTecTroProRestriction =
                    UserDAO.isTecTroProRestriction(
                        connection,
                        sUserIpn,
                        oFopSearch.getFamille(),
                        oFopSearch.getUsine());
                oLogService.debug(
                    "FopMontageSearchDAO.getFopAList() : Restrictions techniques/tronçons="
                        + bTecTroProRestriction);
            }
        }

        // Sql
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            // Usine FOP
            if (oFopSearch
                .getUsine()
                .toUpperCase()
                .equals(Constants.CST_DEFAULT_FACTORY)) {
                pstmt = connection.prepareStatement(SEARCH_FOP_USI_FOP);
                oLogService.debug(SEARCH_FOP_USI_FOP);
                
            }
            // Usine non FOP
            else {
                pstmt = connection.prepareStatement(SEARCH_FOP_USI_NON_FOP);
				oLogService.debug(SEARCH_FOP_USI_FOP);
            }


            pstmt.setString(1 ,oFopSearch.getVue());
            pstmt.setString(2 ,oFopSearch.getVue());
            pstmt.setString(3 ,oUsine.getUsineId());
            pstmt.setString(4 ,oFopSearch.getFamille());
            pstmt.setString(5 ,oFopSearch.getIdFopA());
            pstmt.setString(6 ,sZoneVehSearch);
            pstmt.setString(7 ,oFopSearch.getIdFopAIndice());
            pstmt.setString(8 ,oFopSearch.getIpn());
            pstmt.setString(9 ,oFopSearch.getRedacteur());
            pstmt.setString(10,oFopSearch.getCriticite());
            pstmt.setString(11,oFopSearch.getCriticite());
            pstmt.setString(12,oFopSearch.getCriticite());
            pstmt.setString(13,oFopSearch.getCriticite());
            pstmt.setString(14,oFopSearch.getEtat());
            pstmt.setString(15,oFopSearch.getLangueDoc());
            pstmt.setString(16,oUsine.getUsineId());
            pstmt.setString(17,oFopSearch.getFamille());

            pstmt.setString(18,sTechniqueSearch);
            pstmt.setString(19,sTronconSearch);

            pstmt.setString(20,oFopSearch.getTechnique());
            pstmt.setString(21,oFopSearch.getTroncon());

            pstmt.setString(22,oFopSearch.getTechnique());
            pstmt.setString(23,oFopSearch.getTroncon());
            pstmt.setString(24,oFopSearch.getFamille());

            pstmt.setString(25,oFopSearch.getTechnique());
            pstmt.setString(26,oFopSearch.getTroncon());
            pstmt.setString(27,oFopSearch.getFamille());
            pstmt.setString(28,oUsine.getUsineId());

            pstmt.setString(29,oFopSearch.getRmtc());
            pstmt.setString(30,oFopSearch.getGFE());
            pstmt.setString(31,oFopSearch.getFamille());
            pstmt.setString(32,oFopSearch.getRmtc());
            pstmt.setString(33,oFopSearch.getGFE());
            pstmt.setString(34,oFopSearch.getFF());
            pstmt.setString(35,oFopSearch.getPG());
            pstmt.setString(36,oFopSearch.getRef());
            pstmt.setString(37,oFopSearch.getFF());
            pstmt.setString(38,oFopSearch.getFamille());
            pstmt.setString(39,oFopSearch.getPG());
            pstmt.setString(40,oFopSearch.getRef());
            pstmt.setString(41,oFopSearch.getFF());
            pstmt.setString(42,oFopSearch.getPG());
            pstmt.setString(43,oFopSearch.getRef());
            pstmt.setString(44,oFopSearch.getFamille());
            pstmt.setString(45,oFopSearch.getFF());
            pstmt.setString(46,oFopSearch.getPG());
            pstmt.setString(47,oFopSearch.getRef());
            pstmt.setString(48,oFopSearch.getVeh());
            pstmt.setString(49,oFopSearch.getFamille());
            pstmt.setString(50,oFopSearch.getVeh());
            pstmt.setString(51,oFopSearch.getLibFop());
            pstmt.setString(52,oFopSearch.getLibFop());
            pstmt.setString(53,oFopSearch.getLangueDoc());

            pstmt.setString(54,oFopSearch.getLangueDoc());


            rs = pstmt.executeQuery();

            /*
                select
                   1 DECODE(:1,'PRO',USI.S012_TEC_ID||USI.S015_TRO_ID,FOPA_ZONE_ID),
                   2 DECODE(:2,'PRO',S015_LB,LZON_LIBELLE),
                   3 FOPA_ID,
                   4 FOPA_NUMERO,
                   5 LIND_NOM,
                   6 STRAGG(FOP.INDI_ID||',')
             */

            VueFopMontage oVueFop    = new VueFopMontage();
            FopA oFopA               = new FopA();
            String sIdFopAIndiceList = "";
            String sIdVue            = "";
            String sIdVuePrec        = "";
            boolean bAjoutVue        = false;
            int iCompteurVue         = 0;

            // Constitution de la liste des FOP A par vue : Process (technique/tronçon) ou Zone Véhicule
            while (rs.next()) {

                // Id de la vue
                sIdVue  = rs.getString(1);

                // Nouvelle ligne de vue (Technique/Tronçon ou Zone Véhicule)
                if (!sIdVue.equals(sIdVuePrec)){

                    // Nouvelle vue
                    oVueFop =
                        new VueFopMontage(
                            oUsine.getUsineId(),
                            oUsine.getLibUsine(),
                            sIdVue,
                            rs.getString(2),
                            oFopSearch.getVue(),
                            rs.getString(7));

                    // A priori,on peut ajouter cette vue à la liste
                    bAjoutVue = true;

                    // Sauvegarde l'id de la vue
                    sIdVuePrec = sIdVue;

                    // Si l'ipn de l'utilisateur a une restriction sur des techniques tronçons
                    // on contrôle s'il peut y accéder en case de vue Process
                    if ((oVueFop.getType().equals(Constants.CST_PRO))
                        && bTecTroProRestriction) {

                        bAjoutVue = false;

                        if ((TechDAO
                            .checkTechRight(
                                connection,
                                sUserIpn,
                                oFopSearch.getFamille(),
                                Constants.CST_RFOP,
                                oUsine.getUsineId(),
                                oVueFop.getTechnique(),
                                bIsAdmin)
                            && TronconDAO.checkSectionRight(
                                connection,
                                sUserIpn,
                                oFopSearch.getFamille(),
                                Constants.CST_RFOP,
                                oUsine.getUsineId(),
                                oVueFop.getTechnique(),
                                oVueFop.getTroncon(),
                                bIsAdmin))) {
                            bAjoutVue = true;
                        }
                    } // end of if

                    // Ajout de la vue à la liste
                    if (bAjoutVue) {
                        oList.add(oVueFop);
                        // Comptage des vues
                         iCompteurVue++;
                    }

                } // end of if

                // On ajoute des FOP A à la vue uniquement s'il n'y a qu'une seule vue dans la liste,
                // et cette vue est déployée. S'il y a plus d'une seule vue,la première vue aura sa liste
                // de FOP A effacée aprés la boucle.
                if (iCompteurVue==1) {

                         // Nouvelle FOP
                    oFopA =
                        new FopA(
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                    /*added by munus*/
                    rs.getString(7),
                    rs.getString("OPS_ERROR_FLAG"));

                    // Liste des id des FOP Indicée d'une FOP
                    sIdFopAIndiceList = rs.getString(6);

                    // Suppression du , en fin de la liste des id des FOP Indicée d'une FOP
                    if (sIdFopAIndiceList!=null) {

                        sIdFopAIndiceList =
                            sIdFopAIndiceList.substring(
                                0,
                                sIdFopAIndiceList.length() - 1);

                        // Cas particulier dans lequel on recherche un indice et celui-ci est archivé
                        if (isIndiceArchive) {
                            sIdFopAIndiceList = "-1";
                        }
                    } else {
                        sIdFopAIndiceList = "";
                    }

                    // Mise à jour de la liste de FOP A Indice de la FOP A
                    oFopA.setIdFopAIndiceList(sIdFopAIndiceList);

                    // Ajout de la FOP A à la liste des FOP A de la vue
                    oVueFop.getFopAList().add(oFopA);

                    // La vue est déployée
                    oVueFop.setDeploy(true);

                } // end of if

            } // end of while

            // S'il y a plus d'une vue,on efface la liste des FOP A dans la première vue qui n'est plus déployée
            if (iCompteurVue>1) {
                 ((VueFopMontage)oList.get(0)).setDeploy(false);
                 ((VueFopMontage)oList.get(0)).setFopAList(new ArrayList());
            }

            oLogService.debug("FIN  FopMontageSearchDAO.getFopAList()");

            return oList;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
        }
    }

    /**
     * Recherche des attributs d'une Fop A Indicée.
     *
     * @param connection
     *            the connection
     * @param sUsineId
     *            the s usine id
     * @param sUsine
     *            the s usine
     * @param sIdFopAIndice
     *            the s id fop a indice
     * @param sUserIpn
     *            the s user ipn
     * @param sLangueDoc
     *            the s langue doc
     * @param bTecTroProRestriction
     *            the b tec tro pro restriction
     * @param sFamily
     *            the s family
     *
     * @return the fop a indice
     *
     * @throws SQLException
     *             the SQL exception
     *
     * @author Emmanuel Bartoli (ATOS ORIGIN)
     * @version 20/05/2008 Lot 820 - Recherche du dernier indice officiel
     */
    public static FopAIndice getFopAIndice(
                Connection connection,
                String sUsineId,
                String sUsine,
                String sIdFopAIndice,
                String sUserIpn,
                String sLangueDoc,
                boolean bTecTroProRestriction
    /*added by munus*/
    , String sFamily) throws SQLException {

        // Init the logger
        init();
        oLogService.debug("DEBUT  FopMontageSearchDAO.getFopAIndice()");

        //FOP A Indicée
        FopAIndice oFop = null;

        // Sql
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            // Usine FOP
            if (sUsineId.equals(Constants.CST_DEFAULT_FACTORY)) {
                pstmt = connection.prepareStatement(GET_FOP_A_IND_USI_FOP);
            }
            // Usine non FOP
            else {
                pstmt = connection.prepareStatement(GET_FOP_A_IND_USI_NON_FOP);
            }
            pstmt.setString(1 ,sIdFopAIndice);
            pstmt.setString(2 ,sIdFopAIndice);
            pstmt.setString(3 ,sFamily);
            pstmt.setString(4 ,sLangueDoc);
            pstmt.setString(5 ,sUsineId);

            rs = pstmt.executeQuery();

            /*
                select
                   1 IND.INDI_ID,
                   2 FOPA_ID,
                   3 IND.INDI_ETAT,
                   4 IND.INDI_INDICE,
                   5 IND.INDI_UTIL_IPN,
                   6 LIND_NOM,
                   7 INDI_S015_S012_TEC_ID,
                   8 INDI_S015_TRO_ID,
                   9 DATE_DEB,
                  10 DATE_FIN,
                  11 SOURCE,
                  12 DERN_IND_OFF
                  13 TEC_TRO
                  14 ALL_FAMILIES
             */

            String  sTecTroBlocList = "";

            // Lecture de la FOP A Indicée
            if (rs.next()) {

                // Liste des techniques tronçons des blocs d'une FOP A
                sTecTroBlocList = rs.getString(13);
                if (sTecTroBlocList == null) {
                    sTecTroBlocList = "";
                }

                // Emmanuel Bartoli (ATOS ORIGIN) le 20/05/2008
                // Lot 820 - Ajout de la technique/tronçon de la FOP avant celles des blocs et séparée par un /
                sTecTroBlocList =
                    rs.getString(7)
                        + " "
                        + rs.getString(8)
                        + "/"
                        + sTecTroBlocList;


                // Si l'ipn de l'utilisateur a une restriction sur des techniques tronçons
                // on contrôle s'il peut accéder à cette FOP
                if ((!bTecTroProRestriction)
                    || (UserDAO
                        .checkTecTroRightToConsultFopInd(
                            connection,
                            rs.getString(1),
                            sUserIpn,
                            sUsine))) {
                        oFop =
                        new FopAIndice(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getString(10),
                            rs.getString(11).equals(Constants.CST_O),
                            rs.getString(12).equals(Constants.CST_O),
                            //sTecTroBlocList,
                            /*added by munus */
                            addHtmlTag(sTecTroBlocList,25,"<BR>"),
                              rs.getString(14));
                              tempStr = "";
                }

            }

            oLogService.debug("FIN  FopMontageSearchDAO.getFopAIndice()");

            return oFop;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
        }
    }

    /**
     * Recherche de la liste des FOP A Archivées d'une Fop A.
     *
     * @param connection
     *            the connection
     * @param sFamille
     *            the s famille
     * @param sTechnique
     *            the s technique
     * @param sTroncon
     *            the s troncon
     * @param sNumero
     *            the s numero
     * @param sUserIpn
     *            the s user ipn
     * @param bIsAdmin
     *            the b is admin
     * @param bTecTroProRestriction
     *            the b tec tro pro restriction
     * @param sIndice
     *            the s indice
     *
     * @return the fop a archive list
     *
     * @throws SQLException
     *             the SQL exception
     */
    public static List getFopAArchiveList(
                Connection connection,
                String sFamille,
                String sTechnique,
                String sTroncon,
                String sNumero,
                String sIndice,
                String sUserIpn,
                boolean bIsAdmin,
                boolean bTecTroProRestriction)
        throws SQLException {

        // Init the logger
        init();
        oLogService.debug("DEBUT  FopMontageSearchDAO.getFopAArchiveList()");

        // Liste de FOP A Archivées
        List oFopAArchiveList = new ArrayList();

        // Sql
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            pstmt = connection.prepareStatement(SEARCH_FOP_ARCH);

            pstmt.setString(1 ,sFamille);
            pstmt.setString(2 ,sTechnique);
            pstmt.setString(3 ,sTroncon);
            pstmt.setString(4 ,sNumero);
            pstmt.setString(5 ,sIndice);

            rs = pstmt.executeQuery();

            /*
                select
                   1 ARCH_INDICE,
                   2 LARC_NOM,
                   3 ARCH_S015_S012_TEC_ID,
                   4 ARCH_S015_TRO_ID
                   5 S005_LAN_AQR
             */

            // Lecture des  FOP A Archivées
            while (rs.next()) {

                // Une fop  est ajoutée à la liste
                // Si l'ipn de l'utilisateur a une restriction sur des techniques tronçons
                // on contrôle s'il peut accéder à cette FOP
                if ((!bTecTroProRestriction)
                    || (TechDAO
                            .checkTechRight(
                                connection,
                                sUserIpn,
                                sFamille,
                                Constants.CST_RFOP,
                                Constants.CST_DEFAULT_FACTORY,
                                rs.getString(3),
                                bIsAdmin)
                            && TronconDAO.checkSectionRight(
                                connection,
                                sUserIpn,
                                sFamille,
                                Constants.CST_RFOP,
                                Constants.CST_DEFAULT_FACTORY,
                                rs.getString(3),
                                rs.getString(4),
                                bIsAdmin))) {
                    oFopAArchiveList.add(
                        new FopAArchive(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(5)));
                }
            }

            oLogService.debug("FIN  FopMontageSearchDAO.getFopAArchiveList()");

            return oFopAArchiveList;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (java.sql.SQLException uncatched) {
                    oLogService.warning(uncatched.getMessage());
                }
            }
        }
    }

    /*public static void main(String ar[]){

        String test1 = "12 m/34 1,56 7,89 1,34 1,56 7,89 1,34 1,56 7,89 1,34 1,56 7,89 1,34 1,56 7,89 1,34 1,56 7,89 1,34 1,56 7,89 1 ";
        int maxLimit = 25;
        String splitHtmlStr = "<BR>";
        System.out.println("Str result --->:"+addHtmlTag(test1,maxLimit,splitHtmlStr));

    }*/
// added by munus z000144
   /** The temp str. */
    private static String tempStr = "";


/**
 * Adds the html tag.
 *
 * @param maxLimit
 *            the max limit
 * @param splitHtmlStr
 *            the split html str
 * @param tecTroStr
 *            the tec tro str
 *
 * @return the string
 */
         private static String addHtmlTag(String tecTroStr,int maxLimit,String splitHtmlStr) {
               //System.out.println("munus 2 :: "+test1);
                 int strLen = tecTroStr.length();
                 String subStr = "";
                 if(strLen > maxLimit){

                     tempStr += tecTroStr.substring(0,maxLimit)+splitHtmlStr;
                     subStr = tecTroStr.substring(maxLimit);
                           addHtmlTag(subStr,maxLimit,splitHtmlStr);

                 } else {
                     tempStr += tecTroStr;
                 }
                 return tempStr;
         }


}
