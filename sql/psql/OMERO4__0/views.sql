--
-- Copyright 2008 Glencoe Software, Inc. All rights reserved.
-- Use is subject to license terms supplied in LICENSE.txt
--

-- This file was generated by dsl/resources/ome/dsl/views.vm
-- and can be used to overwrite the generated Map<Long, Long> tables
-- with functional views.

BEGIN;

  DROP TABLE count_Plate_screenLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_screenLinks_by_owner (Plate_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ScreenPlateLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Plate_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_annotationLinks_by_owner (Plate_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PlateAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Pixels_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_pixelsFileMaps_by_owner (Pixels_id, owner_id, count) AS select child, owner_id, count(*)
    FROM PixelsOriginalFileMap GROUP BY child, owner_id ORDER BY child;

  CREATE OR REPLACE FUNCTION channel_pixels_index_move() RETURNS "trigger" AS '
    DECLARE
      duplicate INT8;
    BEGIN

      -- Avoids a query if the new and old values of x are the same.
      IF new.pixels = old.pixels AND new.pixels_index = old.pixels_index THEN
          RETURN new;
      END IF;

      -- At most, there should be one duplicate
      SELECT id INTO duplicate
        FROM channel
       WHERE pixels = new.pixels AND pixels_index = new.pixels_index
      OFFSET 0
       LIMIT 1;

      IF duplicate IS NOT NULL THEN
          RAISE NOTICE ''Remapping channel % via (-1 - oldvalue )'', duplicate;
          UPDATE channel SET pixels_index = -1 - pixels_index WHERE id = duplicate;
      END IF;

      RETURN new;
    END;' LANGUAGE plpgsql;

  CREATE TRIGGER channel_pixels_index_trigger
        BEFORE UPDATE ON channel
        FOR EACH ROW EXECUTE PROCEDURE channel_pixels_index_move ();

  DROP TABLE count_Pixels_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_annotationLinks_by_owner (Pixels_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PixelsAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ExperimenterGroup_groupExperimenterMap_by_owner;

  CREATE OR REPLACE VIEW count_ExperimenterGroup_groupExperimenterMap_by_owner (ExperimenterGroup_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM GroupExperimenterMap GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ExperimenterGroup_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_ExperimenterGroup_annotationLinks_by_owner (ExperimenterGroup_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ExperimenterGroupAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_plateLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_plateLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenPlateLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_annotationLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_pixelsFileMaps_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PixelsOriginalFileMap GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_annotationLinks_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM OriginalFileAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  CREATE OR REPLACE FUNCTION channelbinding_renderingDef_index_move() RETURNS "trigger" AS '
    DECLARE
      duplicate INT8;
    BEGIN

      -- Avoids a query if the new and old values of x are the same.
      IF new.renderingDef = old.renderingDef AND new.renderingDef_index = old.renderingDef_index THEN
          RETURN new;
      END IF;

      -- At most, there should be one duplicate
      SELECT id INTO duplicate
        FROM channelbinding
       WHERE renderingDef = new.renderingDef AND renderingDef_index = new.renderingDef_index
      OFFSET 0
       LIMIT 1;

      IF duplicate IS NOT NULL THEN
          RAISE NOTICE ''Remapping channelbinding % via (-1 - oldvalue )'', duplicate;
          UPDATE channelbinding SET renderingDef_index = -1 - renderingDef_index WHERE id = duplicate;
      END IF;

      RETURN new;
    END;' LANGUAGE plpgsql;

  CREATE TRIGGER channelbinding_renderingDef_index_trigger
        BEFORE UPDATE ON channelbinding
        FOR EACH ROW EXECUTE PROCEDURE channelbinding_renderingDef_index_move ();

  CREATE OR REPLACE FUNCTION codomainmapcontext_renderingDef_index_move() RETURNS "trigger" AS '
    DECLARE
      duplicate INT8;
    BEGIN

      -- Avoids a query if the new and old values of x are the same.
      IF new.renderingDef = old.renderingDef AND new.renderingDef_index = old.renderingDef_index THEN
          RETURN new;
      END IF;

      -- At most, there should be one duplicate
      SELECT id INTO duplicate
        FROM codomainmapcontext
       WHERE renderingDef = new.renderingDef AND renderingDef_index = new.renderingDef_index
      OFFSET 0
       LIMIT 1;

      IF duplicate IS NOT NULL THEN
          RAISE NOTICE ''Remapping codomainmapcontext % via (-1 - oldvalue )'', duplicate;
          UPDATE codomainmapcontext SET renderingDef_index = -1 - renderingDef_index WHERE id = duplicate;
      END IF;

      RETURN new;
    END;' LANGUAGE plpgsql;

  CREATE TRIGGER codomainmapcontext_renderingDef_index_trigger
        BEFORE UPDATE ON codomainmapcontext
        FOR EACH ROW EXECUTE PROCEDURE codomainmapcontext_renderingDef_index_move ();

  DROP TABLE count_Annotation_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Annotation_annotationLinks_by_owner (Annotation_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM AnnotationAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_datasetLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ProjectDatasetLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_annotationLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ProjectAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ScreenAcquisition_wellSampleLinks_by_owner;

  CREATE OR REPLACE VIEW count_ScreenAcquisition_wellSampleLinks_by_owner (ScreenAcquisition_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAcquisitionWellSampleLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ScreenAcquisition_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_ScreenAcquisition_annotationLinks_by_owner (ScreenAcquisition_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAcquisitionAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_WellSample_screenAcquisitionLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_screenAcquisitionLinks_by_owner (WellSample_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ScreenAcquisitionWellSampleLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_WellSample_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_annotationLinks_by_owner (WellSample_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellSampleAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Job_originalFileLinks_by_owner;

  CREATE OR REPLACE VIEW count_Job_originalFileLinks_by_owner (Job_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM JobOriginalFileLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_reagentLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_reagentLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellReagentLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_annotationLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Node_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Node_annotationLinks_by_owner (Node_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM NodeAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Experimenter_groupExperimenterMap_by_owner;

  CREATE OR REPLACE VIEW count_Experimenter_groupExperimenterMap_by_owner (Experimenter_id, owner_id, count) AS select child, owner_id, count(*)
    FROM GroupExperimenterMap GROUP BY child, owner_id ORDER BY child;

  CREATE OR REPLACE FUNCTION groupexperimentermap_child_index_move() RETURNS "trigger" AS '
    DECLARE
      duplicate INT8;
    BEGIN

      -- Avoids a query if the new and old values of x are the same.
      IF new.child = old.child AND new.child_index = old.child_index THEN
          RETURN new;
      END IF;

      -- At most, there should be one duplicate
      SELECT id INTO duplicate
        FROM groupexperimentermap
       WHERE child = new.child AND child_index = new.child_index
      OFFSET 0
       LIMIT 1;

      IF duplicate IS NOT NULL THEN
          RAISE NOTICE ''Remapping groupexperimentermap % via (-1 - oldvalue )'', duplicate;
          UPDATE groupexperimentermap SET child_index = -1 - child_index WHERE id = duplicate;
      END IF;

      RETURN new;
    END;' LANGUAGE plpgsql;

  CREATE TRIGGER groupexperimentermap_child_index_trigger
        BEFORE UPDATE ON groupexperimentermap
        FOR EACH ROW EXECUTE PROCEDURE groupexperimentermap_child_index_move ();

  DROP TABLE count_Experimenter_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Experimenter_annotationLinks_by_owner (Experimenter_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ExperimenterAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  CREATE OR REPLACE FUNCTION pixels_image_index_move() RETURNS "trigger" AS '
    DECLARE
      duplicate INT8;
    BEGIN

      -- Avoids a query if the new and old values of x are the same.
      IF new.image = old.image AND new.image_index = old.image_index THEN
          RETURN new;
      END IF;

      -- At most, there should be one duplicate
      SELECT id INTO duplicate
        FROM pixels
       WHERE image = new.image AND image_index = new.image_index
      OFFSET 0
       LIMIT 1;

      IF duplicate IS NOT NULL THEN
          RAISE NOTICE ''Remapping pixels % via (-1 - oldvalue )'', duplicate;
          UPDATE pixels SET image_index = -1 - image_index WHERE id = duplicate;
      END IF;

      RETURN new;
    END;' LANGUAGE plpgsql;

  CREATE TRIGGER pixels_image_index_trigger
        BEFORE UPDATE ON pixels
        FOR EACH ROW EXECUTE PROCEDURE pixels_image_index_move ();

  DROP TABLE count_Image_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_datasetLinks_by_owner (Image_id, owner_id, count) AS select child, owner_id, count(*)
    FROM DatasetImageLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Image_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_annotationLinks_by_owner (Image_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ImageAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Channel_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Channel_annotationLinks_by_owner (Channel_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ChannelAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_PlaneInfo_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_PlaneInfo_annotationLinks_by_owner (PlaneInfo_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PlaneInfoAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Reagent_wellLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_wellLinks_by_owner (Reagent_id, owner_id, count) AS select child, owner_id, count(*)
    FROM WellReagentLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Reagent_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_annotationLinks_by_owner (Reagent_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ReagentAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_projectLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_projectLinks_by_owner (Dataset_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ProjectDatasetLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Dataset_imageLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_imageLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM DatasetImageLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_annotationLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM DatasetAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Session_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Session_annotationLinks_by_owner (Session_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM SessionAnnotationLink GROUP BY parent, owner_id ORDER BY parent;


--
-- Finally, a function for showing our permissions
-- select id, ome_perms(permissions) FROM sometable...
--
CREATE OR REPLACE FUNCTION ome_perms(p INT8) RETURNS VARCHAR AS '
DECLARE
    ln CHAR DEFAULT ''-'';
    ur CHAR DEFAULT ''-'';
    uw CHAR DEFAULT ''-'';
    gr CHAR DEFAULT ''-'';
    gw CHAR DEFAULT ''-'';
    wr CHAR DEFAULT ''-'';
    ww CHAR DEFAULT ''-'';
BEGIN
    -- shift 8
    SELECT INTO ur CASE WHEN (cast(p as bit(64)) & cast(1024 as bit(64))) = cast(1024 as bit(64)) THEN ''r'' ELSE ''-'' END;
    SELECT INTO uw CASE WHEN (cast(p as bit(64)) & cast( 512 as bit(64))) = cast( 512 as bit(64)) THEN ''w'' ELSE ''-'' END;
    -- shift 4
    SELECT INTO gr CASE WHEN (cast(p as bit(64)) & cast(  64 as bit(64))) = cast(  64 as bit(64)) THEN ''r'' ELSE ''-'' END;
    SELECT INTO gw CASE WHEN (cast(p as bit(64)) & cast(  32 as bit(64))) = cast(  32 as bit(64)) THEN ''w'' ELSE ''-'' END;
    -- shift 0
    SELECT INTO wr CASE WHEN (cast(p as bit(64)) & cast(   4 as bit(64))) = cast(   4 as bit(64)) THEN ''r'' ELSE ''-'' END;
    SELECT INTO ww CASE WHEN (cast(p as bit(64)) & cast(   2 as bit(64))) = cast(   2 as bit(64)) THEN ''w'' ELSE ''-'' END;

    -- shift 18
    -- for high-order bits, logic is reversed
    SELECT INTO ln CASE WHEN (cast(p as bit(64)) & cast(262144 as bit(64))) = cast(262144 as bit(64)) THEN ''-'' ELSE ''L'' END;

    RETURN ln || ur || uw || gr || gw || wr || ww;
END;' LANGUAGE plpgsql;

COMMIT;
