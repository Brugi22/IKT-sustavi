package com.infobip.pmf.dz1.SemanticVersioning;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SemanticVersioning {
    private static final String SEMVER_REGEX = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?(?:\\+[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*)?$";

    public static boolean isValid(String version) {
        Pattern pattern = Pattern.compile(SEMVER_REGEX);
        Matcher matcher = pattern.matcher(version);
        return matcher.matches();
    }

    private String getCoreVersion(String v) {
        return v.split("-")[0].split("\\+")[0];
    }

    private Boolean hasPreRelease(String v) {
        return v.split("\\+")[0].contains("-");
    }

    public String getMaxSemanticVersion(String v1, String v2) {
        if(!isValid(v1)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v1);
        }
        if(!isValid(v2)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v2);
        }

        String coreVersion1 = getCoreVersion(v1);
        String coreVersion2 = getCoreVersion(v2);

        if(coreVersion1.equals(coreVersion2)) {
            return hasPreRelease(v1) ? v2 : v1;
        }else {
            return coreVersion1.compareTo(coreVersion2) < 0 ? v2 : v1;
        }
    }

    public String getNextSemanticVersion(String v, String type) {
        if(!isValid(v)) {
            return "Version \"%s\" does not adhere to the SemVer 2.0.0 specification".formatted(v);
        }
        if(!type.equals("MAJOR") && !type.equals("MINOR") && !type.equals("PATCH")) {
            return "Type \"%s\" is not supported type value".formatted(type);
        }

        int[] versions = Arrays.stream(getCoreVersion(v)
                        .split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();

        switch (type) {
            case "MAJOR" -> {
                versions[0]++;
                versions[1] = 0;
                versions[2] = 0;
            }
            case "MINOR" -> {
                versions[1]++;
                versions[2] = 0;
            }
            case "PATCH" -> versions[2]++;
        }

        return String.format("%d.%d.%d", versions[0], versions[1], versions[2]);
    }
}
