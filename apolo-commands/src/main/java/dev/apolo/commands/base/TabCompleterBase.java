package dev.apolo.commands.base;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterBase {
    public static List<String> filter(List<String> options, String partial) {
        String lowerPartial = partial.toLowerCase();
        return options.stream()
            .filter(opt -> opt.toLowerCase().startsWith(lowerPartial))
            .collect(Collectors.toList());
    }
}
