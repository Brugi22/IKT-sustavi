package com.infobip.pmf.dz1.SemanticVersioning;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SemanticVersioning {
    private static final String SEMVER_REGEX = "^(?<major>(0|[1-9]\\d*))\\." +
                                                "(?<minor>(0|[1-9]\\d*))\\." +
                                                "(?<patch>(0|[1-9]\\d*))" +
                                                "(?:-(?<preRelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?)?" +
                                                "(?:\\+(?<metaData>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";

    public static boolean isValid(String version) {
        Pattern pattern = Pattern.compile(SEMVER_REGEX);
        Matcher matcher = pattern.matcher(version);
        return matcher.matches();
    }

    private Integer[] getCoreVersion(String version) {
        Pattern pattern = Pattern.compile(SEMVER_REGEX);
        Matcher matcher = pattern.matcher(version);
        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group("major"));
            int minor = Integer.parseInt(matcher.group("minor"));
            int patch = Integer.parseInt(matcher.group("patch"));
            return new Integer[]{major, minor, patch};
        } else {
            return null;
        }
    }

    private String getPreRelease(String version) {
        Pattern pattern = Pattern.compile(SEMVER_REGEX);
        Matcher matcher = pattern.matcher(version);
        if (matcher.matches()) {
            return matcher.group("preRelease");
        } else {
            return null;
        }
    }

    private static boolean isPreReleaseGreaterThan(String preRelease1, String preRelease2) {
        if(preRelease1 == null) return true;
        if(preRelease2 == null) return false;

        String[] identifiers1 = preRelease1.split("\\.");
        String[] identifiers2 = preRelease2.split("\\.");

        int length = Math.min(identifiers1.length, identifiers2.length);
        for (int i = 0; i < length; i++) {
            String identifier1 = identifiers1[i];
            String identifier2 = identifiers2[i];
            System.out.println(identifiers1[i] + " " + identifiers2[i]);

            boolean isNumeric1 = identifier1.matches("\\d+");
            boolean isNumeric2 = identifier2.matches("\\d+");

            if (isNumeric1 && isNumeric2) {
                int numericComparison = Integer.compare(Integer.parseInt(identifier1), Integer.parseInt(identifier2));
                if (numericComparison != 0) {
                    return numericComparison > 0;
                }
            }
            else if (!isNumeric1 && isNumeric2) return true;
            else if (isNumeric1 && !isNumeric2) return false;
            else {
                int lexicalComparison = identifier1.compareTo(identifier2);
                if (lexicalComparison != 0) {
                    return lexicalComparison > 0;
                }
            }
        }

        return identifiers1.length > identifiers2.length;
    }

    public String getMaxSemanticVersion(String v1, String v2) {
        if(!isValid(v1)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v1);
        }
        if(!isValid(v2)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v2);
        }

        Integer[] coreVersion1 = getCoreVersion(v1);
        Integer[] coreVersion2 = getCoreVersion(v2);

        if(Arrays.equals(coreVersion1, coreVersion2)) {
            return isPreReleaseGreaterThan(getPreRelease(v1), getPreRelease(v2)) ? v1 : v2;
        }else {
            return Arrays.compare(coreVersion1, coreVersion2) < 0 ? v2 : v1;
        }
    }

    public String getNextSemanticVersion(String v, String type) {
        if(!isValid(v)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v);
        }
        if(!type.equals("MAJOR") && !type.equals("MINOR") && !type.equals("PATCH")) {
            return "Type \"%s\" is not supported type value".formatted(type);
        }

        Integer[] coreVersion = getCoreVersion(v);

        switch (type) {
            case "MAJOR" -> {
                coreVersion[0]++;
                coreVersion[1] = 0;
                coreVersion[2] = 0;
            }
            case "MINOR" -> {
                coreVersion[1]++;
                coreVersion[2] = 0;
            }
            case "PATCH" -> coreVersion[2]++;
        }

        return String.format("%d.%d.%d", coreVersion[0], coreVersion[1], coreVersion[2]);
    }
}
