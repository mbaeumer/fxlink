package se.mbaeumer.fxlink.util;

import se.mbaeumer.fxlink.models.Link;

public interface DescriptionUtil {
    String generateDescription(Link link);
    String removeTheProtocol(Link link);
    String handleSuffix(String description);
}
