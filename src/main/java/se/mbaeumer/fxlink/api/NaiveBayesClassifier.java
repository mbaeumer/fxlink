package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Probability;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;
import java.util.stream.Collectors;

public class NaiveBayesClassifier {

    private final URLHelper urlHelper;
    private final LinkReadDBHandler linkReadDBHandler;
    private final LinkHandler linkHandler;
    private CategoryHandler categoryHandler;

    private List<Link> allLinks;
    private List<Link> allLinksWithCategories;
    private List<Category> categories;

    public NaiveBayesClassifier(URLHelper urlHelper, LinkReadDBHandler linkReadDBHandler, LinkHandler linkHandler) {
        this.urlHelper = urlHelper;
        this.linkReadDBHandler = linkReadDBHandler;
        this.linkHandler = linkHandler;

        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(), new CategoryCreationDBHandler(),
                new CategoryUpdateDBHandler(), new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());

        this.allLinks = this.linkHandler.getLinks();
        this.allLinksWithCategories = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
        this.categories = this.categoryHandler.getCategories();
    }

    public List<Probability> classify(final Link link){
        List<String> words = splitUrl(link);
        List<Probability> probabilities = new ArrayList<>();
        for (Category category : categories){
            probabilities.add(calculateCombinedGeneric(category.getName(), words));
        }

        List<Probability> sorted = probabilities.stream()
                .filter(probability -> !Double.isNaN(probability.getProbability()))
                .sorted(Comparator.comparing(Probability::getProbability).reversed())
                .filter(p -> p.getProbability() > 0.00)
                .collect(Collectors.toList());
        return sorted;
    }

    private List<String> splitUrl(final Link link){
        String url = urlHelper.withoutProtocol(link.getURL());
        url = urlHelper.withoutPrefix(url);
        String[] urlParts = urlHelper.getUrlParts(url);
        List<String> words = new ArrayList<>(Arrays.asList(urlParts));

        List<String> toExclude = words.stream().filter(key -> "for".equals(key)
                || key.length() <=1 || "of".equals(key) || "with".equals(key)
                || "the".equals(key) || "to".equals(key)
                || "com".equals(key) || "de".equals(key) || key.matches(".*\\d.*")
                || "html".equals(key) || "htm".equals(key)).collect(Collectors.toList());
        words.removeAll(toExclude);

        return words;
    }
    public Probability calculateCombinedGeneric(final String categoryName, final List<String> words){
        List<Link> linksInCategory = allLinks.stream()
                .filter(link -> link.getCategory()!=null && categoryName.equals(link.getCategory().getName()))
                .collect(Collectors.toList());

        double pInCategory = (double) linksInCategory.size() / (double) allLinksWithCategories.size();

        Map<String, Double> pWords = new HashMap<>();
        for (String word : words){
            int count = linksInCategory.stream()
                    .filter(link -> link.getURL().contains(word))
                    .collect(Collectors.toList()).size();

            double pWord = ((double) count  + 1) / ((double) linksInCategory.size() + allLinksWithCategories.size());
            pWords.put(word, pWord);
        }

        double dividend = pInCategory * pWords.values().stream().reduce(1.0, (a,b) -> a*b);
        Map<String, Double> pNegated = pWords.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> smooth(1 - e.getValue())
                ));
        double part2 = pNegated.values().stream().reduce(1.0, (a,b) -> a*b);
        double denominator = (dividend + part2);
        double result = dividend / denominator;

        return new Probability(categoryName, result*100.00);
    }

    public double smooth(double value){
        return value == 0 ? 0.0001 : value;
    }
}