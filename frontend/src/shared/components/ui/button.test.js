import { render, screen } from '@testing-library/react';
import { Button } from '@/shared/components/ui/button';

// Mock the clsx and tailwind-merge functions
jest.mock('clsx', () => {
  return {
    __esModule: true,
    default: (...classes) => classes.filter(Boolean).join(' '),
  };
});

jest.mock('tailwind-merge', () => {
  return {
    __esModule: true,
    default: (...classes) => classes.filter(Boolean).join(' '),
  };
});

describe('Button Component', () => {
  test('renders with default props', () => {
    render(<Button>Click me</Button>);
    const buttonElement = screen.getByText(/click me/i);
    expect(buttonElement).toBeInTheDocument();
    expect(buttonElement).toHaveClass('inline-flex');
  });

  test('renders with variant and size props', () => {
    render(<Button variant="destructive" size="lg">Delete</Button>);
    const buttonElement = screen.getByText(/delete/i);
    expect(buttonElement).toBeInTheDocument();
    expect(buttonElement).toHaveClass('bg-red-600');
    expect(buttonElement).toHaveClass('h-12');
  });

  test('renders as disabled when disabled prop is true', () => {
    render(<Button disabled>Disabled Button</Button>);
    const buttonElement = screen.getByText(/disabled button/i);
    expect(buttonElement).toBeInTheDocument();
    expect(buttonElement).toBeDisabled();
  });

  test('renders as loading state when loading prop is true', () => {
    render(<Button loading>Loading...</Button>);
    const buttonElement = screen.getByRole('button');
    expect(buttonElement).toBeInTheDocument();
    expect(buttonElement).toHaveAttribute('aria-disabled', 'true');
  });
});