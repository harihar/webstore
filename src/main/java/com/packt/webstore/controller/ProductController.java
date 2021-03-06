package com.packt.webstore.controller;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.packt.webstore.domain.Product;
import com.packt.webstore.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@RequestMapping
	public String list(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}

	@RequestMapping("/all")
	public String allProducts(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}

	// @RequestMapping("/{category}")
	// public String getProductsByCategory(Model model,
	// @PathVariable("category") String productCategory) {
	// model.addAttribute("products",
	// productService.getProductsByCategory(productCategory));
	// return "products";
	// }

	@RequestMapping("/filter/{ByCriteria}")
	public String getProductsByFilter(
			@MatrixVariable(pathVar = "ByCriteria") Map<String, List<String>> filterParams,
			Model model) {

		model.addAttribute("products",
				productService.getProductsByFilter(filterParams));
		return "products";
	}

	@RequestMapping("/product")
	public String getProductById(@RequestParam("id") String productId,
			Model model) {
		model.addAttribute("product", productService.getProductById(productId));
		return "product";
	}

	@RequestMapping("/{category}")
	public String filterProducts(
			@PathVariable String category,
			@RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
			@RequestParam(value = "maxPrice", defaultValue = "0") BigDecimal maxPrice,
			@RequestParam(value = "manufacturer", defaultValue = "default") String manufacturer,
			Model model) {
		Set<Product> products = new HashSet<>();
		if (!"default".equalsIgnoreCase(manufacturer)) {
			products.addAll(productService
					.getProductsByManufacturer(manufacturer));
		}
		products.addAll(productService.getProductsByCategory(category));
		if (minPrice.compareTo(BigDecimal.ZERO) != 0
				|| maxPrice.compareTo(BigDecimal.ZERO) == 0) {
			products.addAll(productService.getProductsByPriceFilter(minPrice,
					maxPrice));
		}
		model.addAttribute("products", products);

		return "products";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String getAddNewProductForm(
			@ModelAttribute("newProduct") Product newProduct) {
		return "addProduct";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String processAddNewProductForm(
			@ModelAttribute("newProduct") Product newProduct) {
		productService.addProduct(newProduct);
		return "redirect:/products";
	}
}
